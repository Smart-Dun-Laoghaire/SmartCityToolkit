package org.example.smart.smartcitytoolkitforge1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;




public class BlockInteractionHandler {

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);

        // Check if the block has a BlockEntity
        if (world.getBlockEntity(pos) != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            // Get the face of the block that was clicked
            BlockHitResult hitResult = (BlockHitResult) player.pick(5.0, 1.0F, false); // Adjust the distance as needed
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                Direction clickedFace = hitResult.getDirection();

                // Now you have the clicked face and the BlockEntity
                System.out.println("Player right-clicked face: " + clickedFace + " of BlockEntity at: " + pos);

                // You can now perform actions based on the clicked face and BlockEntity
            }
        }
    }






    public String getTargetedFace(float angle, String axis, String face) {

        angle += Math.clamp(angle * 1000, -1, 1) * 45;
        angle = angle % 360;
        angle = angle / 90;
        angle = (float)Math.floor(angle);

        switch(axis){
            case "X":

                // ovisno o danom licu mijenjamo angle
                switch (face){
                    case "UP": angle += 0; break;
                    case "NORTH": angle += 1; break;
                    case "DOWN": angle += 2; break;
                    case "SOUTH": angle += 3; break;
                }

                // vraća korektnu stranu
                switch ((int)(angle % 4)){

                    case 0: return "UP";
                    case 1: return "NORTH";
                    case 2: return "DOWN";
                    case 3: return "SOUTH";

                }
                return face;


            case "Y":

                break;

        }

        return face;

    }




}

