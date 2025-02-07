package org.example.smart.smartcitytoolkitforge1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import static com.mojang.text2speech.Narrator.LOGGER;


@Mod.EventBusSubscriber
public class BlockInteractionHandler {


    /*@SubscribeEvent
    public static void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock event) {

        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof IMUBlockEntity) {
            IMUBlockEntity imuBlockEntity = (IMUBlockEntity) blockEntity;
            imuBlockEntity.setRotation(imuBlockEntity.getRotationX() + 45.1f, 0, 0);
        }


        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }*/


    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();

        if (player.getItemInHand(hand).isEmpty() && level.getBlockEntity(pos) != null) {
            BlockEntity blockEntity = level.getBlockEntity(pos);


            if (blockEntity instanceof IMUBlockEntity) {
                IMUBlockEntity imuBlockEntity = (IMUBlockEntity) blockEntity;

                BlockHitResult hitResult = (BlockHitResult) player.pick(5.0, 1.0F, false);
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    Direction clickedFace = hitResult.getDirection();

                    String targetedFace = "";
                    String currentFace = clickedFace.toString().toUpperCase();
                    int maxFace = 3;
                    boolean xRot = false, yRot = false, zRot = false;

                    for (int i = 0; i < maxFace; i++){



                        if(i % 3 == 0 && !zRot){
                            float rotationZ = imuBlockEntity.getRotationZ();
                            targetedFace = getTargetedFace(rotationZ, "Z", currentFace);
                            if(targetedFace.equalsIgnoreCase(currentFace)) { maxFace++; }
                            else {zRot = true;}
                        }
                        else if(i % 3 == 1 && !yRot){
                            float rotationY = imuBlockEntity.getRotationY();
                            targetedFace = getTargetedFace(rotationY, "Y", currentFace);
                            if(targetedFace.equalsIgnoreCase(currentFace)) { maxFace++; }
                            else {yRot = true;}
                        }
                        else if(i % 3 == 2 && !xRot){
                            float rotationX = imuBlockEntity.getRotationX();
                            targetedFace = getTargetedFace(rotationX, "X", currentFace);
                            if(targetedFace.equalsIgnoreCase(currentFace)) { maxFace++; }
                            else {xRot = true;}
                        }


                        if(i > 3) {
                            if (!zRot) {
                                float rotationZ = imuBlockEntity.getRotationZ();
                                targetedFace = getTargetedFace(rotationZ, "Z", currentFace);
                                if (targetedFace.equalsIgnoreCase(currentFace)) {
                                    maxFace++;
                                } else {
                                    zRot = true;
                                }
                            } else if (!yRot) {
                                float rotationY = imuBlockEntity.getRotationY();
                                targetedFace = getTargetedFace(rotationY, "Y", currentFace);
                                if (targetedFace.equalsIgnoreCase(currentFace)) {
                                    maxFace++;
                                } else {
                                    yRot = true;
                                }
                            } else if (!xRot) {
                                float rotationX = imuBlockEntity.getRotationX();
                                targetedFace = getTargetedFace(rotationX, "X", currentFace);
                                if (targetedFace.equalsIgnoreCase(currentFace)) {
                                    maxFace++;
                                } else {
                                    xRot = true;
                                }

                            }
                        }

                        currentFace = targetedFace;

                        if(i > 6){
                            break;
                        }

                    }

                    System.out.println("Player right-clicked face: " + clickedFace + " of BlockEntity at: " + pos);
                    System.out.println("Targeted Face: " + targetedFace);
                }
            }

            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }



    }






    public static String getTargetedFace(float angle, String axis, String face) {

        angle += Math.clamp(angle * 1000, -1, 1) * 45;
        angle = angle % 360;
        angle = angle / 90;
        angle = (float)Math.floor(angle);

        face = face.toUpperCase();
        axis = axis.toUpperCase();



        if(axis.equalsIgnoreCase("Z")) {

            // ovisno o danom licu mijenjamo angle
            switch (face) {
                case "NORTH":
                    angle += 0;
                    break;
                case "EAST":
                    angle += 1;
                    break;
                case "SOUTH":
                    angle += 2;
                    break;
                case "WEST":
                    angle += 3;
                    break;
                default:
                    return face;
            }


            // vraća korektnu stranu
            switch ((int) (angle % 4)) {
                case 0:
                    return "NORTH";
                case 1:
                    return "EAST";
                case 2:
                    return "SOUTH";
                case 3:
                    return "WEST";
            }
        }


        if(axis.equalsIgnoreCase("Y")) {

            // ovisno o danom licu mijenjamo angle
            switch (face) {
                case "UP":
                    angle += 0;
                    break;
                case "NORTH":
                    angle += 1;
                    break;
                case "DOWN":
                    angle += 2;
                    break;
                case "SOUTH":
                    angle += 3;
                    break;
                default:
                    return face;
            }

            // vraća korektnu stranu
            switch ((int) (angle % 4)) {
                case 0:
                    return "UP";
                case 1:
                    return "NORTH";
                case 2:
                    return "DOWN";
                case 3:
                    return "SOUTH";
            }
        }


        if(axis.equalsIgnoreCase("X")){
            // ovisno o danom licu mijenjamo angle
            switch (face) {
                case "UP":
                    angle += 0;
                    break;
                case "EAST":
                    angle += 1;
                    break;
                case "DOWN":
                    angle += 2;
                    break;
                case "WEST":
                    angle += 3;
                    break;
                default:
                    return face;
            }

            // vraća korektnu stranu
            switch ((int) (angle % 4)) {
                case 0:
                    return "UP";
                case 1:
                    return "EAST";
                case 2:
                    return "DOWN";
                case 3:
                    return "WEST";
            }
        }






        return face;

    }




}

