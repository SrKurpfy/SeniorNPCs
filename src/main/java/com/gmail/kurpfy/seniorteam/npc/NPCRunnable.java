package com.gmail.kurpfy.seniorteam.npc;

import com.gmail.kurpfy.seniorteam.npc.NPC;

public class NPCRunnable implements Runnable {

    private final NPC npc;

    private int step;

    public NPCRunnable(NPC npc) {
        this.npc = npc;
    }

    @Override
    public void run() {
        switch (step) {
            case 3:
            case 0: {
                npc.getPacketController().jump(true);
                break;
            }

            case 4:
            case 1: {
                npc.getPacketController().jump(false);
                break;
            }

            case 9:
            case 6: {
                npc.getPacketController().crouch((byte) 2);
                break;
            }

            case 13:
            case 7: {
                npc.getPacketController().crouch((byte) 0);
                break;
            }

            case 11: {
                npc.getPacketController().hit();
                break;
            }

            case 16: {
                npc.getPacketController().destroy();
            }

        }

        step++;
    }
}
