package com.sdftdusername.saturn.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.sdftdusername.saturn.SaturnMod;
import com.sdftdusername.saturn.mixins.EntityGetSightRange;
import com.sdftdusername.saturn.mixins.EntityModelInstanceGetBoneMap;
import com.sdftdusername.saturn.mixins.ItemEntityGetItemStack;
import de.pottgames.tuningfork.SoundBuffer;
import finalforeach.cosmicreach.GameAssetLoader;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.Threads;
import finalforeach.cosmicreach.audio.SoundManager;
import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.entities.ItemEntity;
import finalforeach.cosmicreach.entities.Player;
import finalforeach.cosmicreach.items.Item;
import finalforeach.cosmicreach.items.ItemBlock;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.rendering.entities.EntityModel;
import finalforeach.cosmicreach.rendering.entities.EntityModelInstance;
import finalforeach.cosmicreach.savelib.crbin.CRBSerialized;
import finalforeach.cosmicreach.world.Zone;

import java.lang.reflect.Field;
import java.util.HashMap;

public class SaturnEntity extends Entity {
    public static final String ENTITY_TYPE_ID = "base:saturn_guy";
    public static final float SPEED = 3f;

    public static SoundBuffer[] yearnSounds = new SoundBuffer[8];
    public static SoundBuffer[] thankSounds = new SoundBuffer[8];

    public static String[] yearnMessages = new String[8];
    public static String[] thankMessages = new String[8];

    public String currentAnimation = "";

    public double nextTalk = 0;
    public boolean isTalking = false;

    public double talkOver = 0;
    public double playTalk = 0;
    public int talkOverState = 0;

    public Object bodyBone = null;

    @CRBSerialized
    public float bodyRotationY = 0;

    @CRBSerialized
    public float headRotationX = 0;

    @CRBSerialized
    public float headRotationY = 0;

    @CRBSerialized
    public boolean gotLog = false;

    public void playAnimation(String name) {
        if (currentAnimation.equals(name))
            return;

        if (model == null) {
            SaturnMod.LOGGER.error("model is null, can't play animation");
            return;
        }

        model.setCurrentAnimation(this, "animation.saturn." + name);
        currentAnimation = name;
    }

    public void talk(Zone zone, Player player, SoundBuffer[] sounds, String[] messages) {
        if (isTalking)
            return;

        isTalking = true;
        nextTalk = MathUtils.random(5f, 8f);

        playAnimation("start_talking");

        int index = MathUtils.random(0, sounds.length - 1);
        SoundManager.INSTANCE.playSound3D(sounds[index], position);

        if (player != null)
            Chat.MAIN_CHAT.sendMessage(zone.getWorld(), player, null, "Saturn> " + messages[index]);

        talkOver = sounds[index].getDuration();
        playTalk = 0.5d;

        talkOverState = 0;
    }

    public void moveToPosition(Vector3 position, float multiply) {

        double yaw = Math.toRadians(bodyRotationY);
        Vector3 forward = new Vector3(
                (float)Math.sin(yaw),
                0,
                (float)Math.cos(yaw)
        );

        float force = SPEED * multiply;
        this.onceVelocity.add(forward.scl(force));
    }

    public void stopMoving() {
        onceVelocity.setZero();
    }

    public SaturnEntity() {
        super("base:entity_saturn");
        canDespawn = false;

        localBoundingBox.min.set(-0.25f, 0, -0.25f);
        localBoundingBox.max.set(0.25f,  2, 0.25f);
        localBoundingBox.update();
        getBoundingBox(this.globalBoundingBox);

        //viewPositionOffset = new Vector3(0, 1.75f, 0);

        Threads.runOnMainThread(
                () -> this.model = GameSingletons.entityModelLoader
                        .load(this, "model_saturn.json", "saturn.animation.json", "animation.saturn.idle", "saturn.png")
        );

        currentAnimation = "idle";

        nextTalk = MathUtils.random(5f, 8f);
    }

    @Override
    public void hit(float amount) {
        super.hit(amount);
    }

    @Override
    protected void onDeath(Zone zone) {
        super.onDeath(zone);
    }

    public boolean setBodyRotation(Vector3 rotation) {
        boolean worked = false;

        if (bodyBone != null) {
            worked = true;

            try {
                Class myClass = bodyBone.getClass();
                Field rotationField = myClass.getField("rotation");
                rotationField.set(bodyBone, rotation);
                worked = true;
            } catch (Exception e) {
                SaturnMod.LOGGER.error(e.getMessage());
                worked = false;
            }
        }

        return worked;
    }

    public void wrap() {
        while (bodyRotationY > 180)
            bodyRotationY -= 360;
        while (bodyRotationY < -180)
            bodyRotationY += 360;

        while (headRotationX > 180)
            headRotationX -= 360;
        while (headRotationX < -180)
            headRotationX += 360;
    }

    public float shortestAngleDifference(float a, float b) {
        float diff = (b - a) % 360;
        if (diff > 180)
            diff -= 360;
        else if (diff < -180)
            diff += 360;
        return diff;
    }

    public float lerpRotation(float a, float b, float t) {
        float diff = shortestAngleDifference(a, b);
        return (a + diff * t + 360) % 360;
    }

    @Override
    public void update(Zone zone, double deltaTime) {
        if (bodyBone == null) {
            EntityModelInstance entityModelInstance = ((EntityModel)model).getModelInstance(this);
            HashMap boneMap = ((EntityModelInstanceGetBoneMap)entityModelInstance).getBoneMap();
            bodyBone = boneMap.get("body");
        }

        boolean canTalk = false;

        Player closestPlayer = getClosestPlayerToEntity(zone);
        if (closestPlayer != null) {
            Entity closestPlayerEntity = closestPlayer.getEntity();

            float[] headRotation = lookAt(
                    position.x,
                    position.y,
                    position.z,
                    closestPlayerEntity.position.x,
                    closestPlayerEntity.position.y,
                    closestPlayerEntity.position.z
            );

            headRotationX -= bodyRotationY;

            headRotationX = lerpRotation(headRotationX, headRotation[0], (float)deltaTime * 5f);
            headRotationY = lerpRotation(headRotationY, headRotation[1], (float)deltaTime * 5f);

            bodyRotationY = lerpRotation(bodyRotationY, -headRotationX + 90, (float) deltaTime * 2.5f);

            headRotationX += bodyRotationY;

            wrap();

            float distance = closestPlayerEntity.position.dst(position);

            if (!isTalking) {
                if (distance > 2.5f && !gotLog) {
                    // Move to player
                    playAnimation("follow");
                    moveToPosition(closestPlayerEntity.position, 1);
                    if (closestPlayerEntity.position.y - position.y > 0.1 && isOnGround) {
                        isOnGround = false;
                        velocity.add(0.0F, 10.0F, 0.0F);
                    }
                } else if (distance < 1.5f) {
                    // Back away from player
                    playAnimation("back_away");
                    moveToPosition(closestPlayerEntity.position, -1);
                } else {
                    // Stop moving
                    playAnimation("idle");
                    stopMoving();

                    canTalk = true;
                }
            } else {
                stopMoving();
            }

            if (!isTalking && !gotLog) {
                nextTalk -= deltaTime;
                if (nextTalk <= 0 && canTalk)
                    talk(zone, closestPlayer, yearnSounds, yearnMessages);
            }
        }

        if (isTalking) {
            if (talkOver > 0)
                talkOver -= deltaTime;

            if (talkOver <= 0) {
                if (talkOverState == 0) {
                    playAnimation("end_talking");

                    talkOverState = 1;
                    talkOver = 0.5d;
                } else {
                    isTalking = false;
                    playTalk = 0;
                }
            } else {
                if (playTalk > 0)
                    playTalk -= deltaTime;

                if (playTalk <= 0 && talkOverState != 1)
                    playAnimation("talk");
            }
        }

        setBodyRotation(new Vector3(0, bodyRotationY, 0));
        viewDirection = convertToDirectionVector(headRotationX, headRotationY);

        try {
            if (!gotLog && !isTalking) {
                for (int i = 0; i < zone.allEntities.size; ++i) {
                    Entity entity = zone.allEntities.get(i);
                    if (entity instanceof ItemEntity) {
                        ItemEntity itemEntity = (ItemEntity) entity;
                        ItemStack itemStack = ((ItemEntityGetItemStack) itemEntity).getItemStack();

                        float dist = position.dst(itemEntity.position);
                        if (dist > 1)
                            continue;

                        Item item = itemStack.getItem();
                        if (item instanceof ItemBlock) {
                            ItemBlock itemBlock = (ItemBlock) item;

                            if (itemBlock.getBlockState().getBlock().getStringId().equals("base:tree_log")) {
                                //((ItemEntityGetItemStack) entity).die(zone);
                                entity.hit(Float.MAX_VALUE);
                                gotLog = true;
                                talk(zone, closestPlayer, thankSounds, thankMessages);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            SaturnMod.LOGGER.error(e.getMessage());
        }

        super.update(zone, deltaTime);
    }

    public static float lookAt(float x1, float y1, float x2, float y2) {
        // Calculate the difference vector
        float dx = x2 - x1;
        float dy = y2 - y1;

        // Compute the angle using atan2
        double angleRadians = Math.atan2(dy, dx);

        // Convert the angle to degrees
        double angleDegrees = Math.toDegrees(angleRadians);

        return (float)angleDegrees;
    }

    public static float[] lookAt(float x1, float y1, float z1, float x2, float y2, float z2) {
        // Calculate the difference vector
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        // Compute the yaw (rotation around the y-axis)
        double yawRadians = Math.atan2(dz, dx);

        // Compute the pitch (rotation around the x-axis)
        double distance = Math.sqrt(dx * dx + dz * dz);
        double pitchRadians = Math.atan2(dy, distance);

        // Convert the angles to degrees
        double yawDegrees = Math.toDegrees(yawRadians);
        double pitchDegrees = Math.toDegrees(pitchRadians);

        return new float[] {(float)yawDegrees, (float)pitchDegrees};
    }

    public Vector3 convertToDirectionVector(float yawDegrees, float pitchDegrees) {
        double yawRadians = Math.toRadians(yawDegrees);
        double pitchRadians = Math.toRadians(pitchDegrees);

        double x = Math.cos(pitchRadians) * Math.cos(yawRadians);
        double y = Math.sin(pitchRadians);
        double z = Math.cos(pitchRadians) * Math.sin(yawRadians);

        // Normalize the vector
        double magnitude = Math.sqrt(x * x + y * y + z * z);

        return new Vector3((float)(x / magnitude), (float)(y / magnitude), (float)(z / magnitude));
    }

    public Player getClosestPlayerToEntity(Zone zone) {
        Array<Player> players = zone.players;
        Player closest = null;
        float closestDst = Float.MAX_VALUE;
        float range = ((EntityGetSightRange)this).getSightRange();

        for (Player p : players) {
            Entity pe = p.getEntity();
            if (pe != null) {
                float dst = position.dst(pe.position);
                if (!(dst > range)) {
                    if (closest == null) {
                        closest = p;
                    } else if (closestDst > dst) {
                        closestDst = dst;
                        closest = p;
                    }
                }
            }
        }

        return closest;
    }

    static {
        for (int i = 0; i < yearnSounds.length; ++i)
            yearnSounds[i] = GameAssetLoader.getSound("assets/sounds/entities/saturn/yearn/" + (i + 1) + ".ogg");

        for (int i = 0; i < thankSounds.length; ++i)
            thankSounds[i] = GameAssetLoader.getSound("assets/sounds/entities/saturn/thank/" + (i + 1) + ".ogg");

        yearnMessages[0] = "I need log.";
        yearnMessages[1] = "May I have a log?";
        yearnMessages[2] = "Log please!";
        yearnMessages[3] = "Please give me log!";
        yearnMessages[4] = "One log please!";
        yearnMessages[5] = "Give me log now!";
        yearnMessages[6] = "I am in need of a log.";
        yearnMessages[7] = "Give me log.";

        thankMessages[0] = "Thank you.";
        thankMessages[1] = "Thank you so much!";
        thankMessages[2] = "I am so thankful!";
        thankMessages[3] = "I needed that.";
        thankMessages[4] = "I am really grateful.";
        thankMessages[5] = "Yay!";
        thankMessages[6] = "Thank you for the log.";
        thankMessages[7] = "Thank you kind sir.";
    }
}
