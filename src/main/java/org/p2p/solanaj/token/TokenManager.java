package org.p2p.solanaj.token;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.programs.AssociatedTokenProgram;
import org.p2p.solanaj.programs.MemoProgram;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.programs.TokenProgram;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;

import java.util.Arrays;

/**
 * Manager class for calling {@link TokenProgram}-related APIs
 */
public class TokenManager {

    private final RpcClient client;

    public TokenManager(final RpcClient client) {
        this.client = client;
    }

    public String transfer(final Account owner, final PublicKey source, final PublicKey destination, final PublicKey tokenMint, long amount) {
        final Transaction transaction = new Transaction();

        // SPL token instruction
        transaction.addInstruction(
                TokenProgram.transfer(
                        source,
                        destination,
                        amount,
                        owner.getPublicKey()
                )
        );

        // Memo
        /*transaction.addInstruction(
                MemoProgram.writeUtf8(
                        owner.getPublicKey(),
                        ""
                )
        );*/

        // Call sendTransaction
        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, owner);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String transferCheckedToSolAddress(final Account owner, final PublicKey source, final PublicKey destination, final PublicKey tokenMint, long amount, byte decimals) {
        // getTokenAccountsByOwner
        PublicKey tokenAccount = null;

        try {
            tokenAccount = client.getApi().getTokenAccountsByOwner(destination, tokenMint);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        final Transaction transaction = new Transaction();
        // SPL token instruction
        transaction.addInstruction(
                TokenProgram.transferChecked(
                        source,
                        tokenAccount,
                        amount,
                        decimals,
                        owner.getPublicKey(),
                        tokenMint
                )
        );

        // Memo
        /*transaction.addInstruction(
                MemoProgram.writeUtf8(
                        owner.getPublicKey(),
                        ""
                )
        );*/

        // Call sendTransaction
        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, owner);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String transferCheckedToSolAddressFundRecipient(final Account owner, final PublicKey source, final PublicKey destination, final PublicKey tokenMint, long amount, byte decimals) throws RpcException {
        // getTokenAccountsByOwner
        PublicKey tokenAccount = destination;

        final Transaction transaction = new Transaction();
        //is spl token address
        if (!isTokenAccount(destination)){
            tokenAccount = AssociatedTokenProgram.getAssociatedTokenAddress(destination, tokenMint);

            boolean needsFunding = !isTokenAccount(tokenAccount);

            if (needsFunding){
                transaction.addInstruction(
                        AssociatedTokenProgram.create(
                                owner.getPublicKey(),
                                tokenMint,
                                destination,
                                tokenAccount
                        )
                );
            }

        }

        // SPL token instruction
        transaction.addInstruction(
                TokenProgram.transferChecked(
                        source,
                        tokenAccount,
                        amount,
                        decimals,
                        owner.getPublicKey(),
                        tokenMint
                )
        );

        // Call sendTransaction
        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, owner);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String initializeAccount(Account newAccount, PublicKey usdcTokenMint, Account owner) {
        final Transaction transaction = new Transaction();

        // SPL token instruction
        transaction.addInstruction(
                TokenProgram.initializeAccount(
                        newAccount.getPublicKey(),
                        usdcTokenMint,
                        owner.getPublicKey()
                )
        );

        // Call sendTransaction
        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, owner);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean isTokenAccount(PublicKey destination) throws RpcException {
        boolean ret = false;

        AccountInfo accountInfo = client.getApi().getAccountInfo(destination);
        if (accountInfo == null || accountInfo.getValue() == null || SystemProgram.PROGRAM_ID.toBase58().equalsIgnoreCase(accountInfo.getValue().getOwner())){
            ret = false;
        }else if(TokenProgram.PROGRAM_ID.toBase58().equalsIgnoreCase(accountInfo.getValue().getOwner())){
            ret = true;
        }else{
            throw new RpcException("Unsupported recipient address: "+destination.toString());
        }

        return ret;

    }
}
