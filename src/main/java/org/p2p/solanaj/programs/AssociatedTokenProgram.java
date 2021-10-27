package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Sysvar;
import org.p2p.solanaj.core.TransactionInstruction;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssociatedTokenProgram  extends Program {

    public static final PublicKey PROGRAM_ID = new PublicKey("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL");

    private static final int CREATE_METHOD_ID = 0;


    public static PublicKey getAssociatedTokenAddress(PublicKey wallet_address, PublicKey spl_token_mint_address){
        try {
            PublicKey.ProgramDerivedAddress programAddress = PublicKey
                    .findProgramAddress(
                            Arrays.asList(wallet_address.toByteArray(),
                                    TokenProgram.PROGRAM_ID.toByteArray(),
                                    spl_token_mint_address.toByteArray()),
                            PROGRAM_ID);

            return programAddress.getAddress();

        } catch (Exception e) {

        }

        return null;
    }


    public static TransactionInstruction create(PublicKey fee_payer, PublicKey spl_token_mint_address, PublicKey destination, PublicKey associated_destination) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(fee_payer,true, true));
        keys.add(new AccountMeta(associated_destination,false, true));
        keys.add(new AccountMeta(destination,false, false));
        keys.add(new AccountMeta(spl_token_mint_address,false, false));
        keys.add(new AccountMeta(SystemProgram.PROGRAM_ID,false, false));
        keys.add(new AccountMeta(TokenProgram.PROGRAM_ID,false, false));
        keys.add(new AccountMeta(Sysvar.SYSVAR_RENT_ADDRESS,false, false));

        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) CREATE_METHOD_ID);

        byte[] transactionData = buffer.array();

        return createTransactionInstruction(
                PROGRAM_ID,
                keys,
                transactionData
        );
    }


}
