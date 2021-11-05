package org.p2p.solanaj.core;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import jdk.nashorn.internal.parser.JSONParser;
import lombok.Builder;
import org.bitcoinj.core.Base58;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.Block;
import org.p2p.solanaj.rpc.types.BlockTransaction;
import org.p2p.solanaj.rpc.types.RpcResponse;
import org.p2p.solanaj.token.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class SolTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SolTest.class);

    private final RpcClient client = new RpcClient(Cluster.DEVNET);
    public final TokenManager tokenManager = new TokenManager(client);

//    private static final PublicKey USDC_TOKEN_MINT = new PublicKey("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v"); //mainnet
    private static final PublicKey USDC_TOKEN_MINT = new PublicKey("AkUFCWTXb3w9nY2n6SFJvBV6VwvFUCe4KBMCcgLsa2ir");
    private static final long LAMPORTS_PER_SOL = 1000000000L;


    public static Account testAccount;
    public static PublicKey solDestination;
    public static PublicKey solDestinationExistToken;
    public static PublicKey usdcSource;
    public static PublicKey usdcDestination;

    public static final String mnemonic = "stick illness arrow dizzy input sock wealth style gather ridge special silly";


    @BeforeClass
    public static void setup() {

        List<String> words = Arrays.asList( mnemonic.split(" "));
        try {
            testAccount = Account.fromBip39Mnemonic(words, "");
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        try {
            solDestination = new PublicKey("BYVsRk9ph32ggPjgEzxHiiDTGbTkwGt2HCeCKEGkuAuB");
            solDestinationExistToken = new PublicKey("JBD2bQzMWzmB1U2NiNzjJKVMpHhRHu5KrPRGL5ywakNg");
            usdcSource = new PublicKey("4LBZdQZ51QVVMryLzxH5hPCfkXTbitwYi7ngoxBU2bFr");
            usdcDestination = new PublicKey("3zweiiPkGNNEtTZZky4CDTuPa3FyRc8ZSAsZj8ptcNGu");
        } catch (Exception ex) {
            LOGGER.error("", ex);
        }
    }

    @Test
    public void generateAddress(){
        Account account = Account.generateAddress();
        System.out.println(String.format("publicKey: %s", account.getPublicKey().toBase58()));
        System.out.println(String.format("secretKey: %s", Base58.encode(account.getSecretKey())));
    }

    @Test
    public void createAddress(){
        Account acc = testAccount;

        System.out.println(acc.getPublicKey().toString());
        System.out.println(Base58.encode(acc.getSecretKey()));

        Account account = new Account(Base58.decode(Base58.encode(acc.getSecretKey())));
        System.out.println(account.getPublicKey().toString());
    }



    @Test
    @Ignore
    public void transactionMemoTest() {
        final int lamports = 1337;
        final PublicKey destination = solDestination;

        // Create account from private key
        final Account feePayer = testAccount;

        final Transaction transaction = new Transaction();
        transaction.addInstruction(
                SystemProgram.transfer(
                        feePayer.getPublicKey(),
                        destination,
                        lamports
                )
        );

        // Call sendTransaction
        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, feePayer);
            LOGGER.info("Result = " + result);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        assertNotNull(result);
    }


    @Test
    public void getBlockHeightTest() {
        try {
            long blockHeight = client.getApi().getBlockHeight();
            LOGGER.info(String.format("Block height = %d", blockHeight));
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getBalanceTest() {
        try {
            long balance = client.getApi().getBalance(solDestination);
            LOGGER.info(String.format("balance = %d", balance));
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBlockTest() {
        try {
            Block block = client.getApi().getBlock(91662238);

            List<BlockTransaction> transactionList = block.getTransactions();
            for (int i = 0; i < transactionList.size(); i++) {
                if (i == 117) {
                    BlockTransaction blockTransaction = transactionList.get(i);
                    List<BlockTransaction.Instruction> instructionList = blockTransaction.getTransaction().getMessage().getInstructions();
                    for (BlockTransaction.Instruction instruction : instructionList) {

                        Object parsed = instruction.getParsed();


                        System.out.println(parsed);
                    }

                }
            }

            System.out.println(block);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void transferCheckedTest() {
        final PublicKey source = usdcSource; // Private key's USDC token account
        final PublicKey destination = new PublicKey("BMbZAciJJKAveJ6TmioYJSA7c31ikJVLqL1XbPUDHG5J");

        /*
            amount = "0.0001" usdc
            amount = 100
            decimals = 6
         */

        final long tokenAmount = 0;
        final byte decimals = 9;

        // Create account from private key
        final Account owner = testAccount;

        try {
            final String txId = tokenManager.transferCheckedToSolAddressFundRecipient(
                    owner,
                    source,
                    destination,
                    USDC_TOKEN_MINT,
                    tokenAmount,
                    decimals
            );
            System.out.println(txId);

            // TODO - actually verify something
            assertNotNull(txId);
        } catch (RpcException e) {
            e.printStackTrace();
        }


    }



    @Test
    public void transferCheckedTest2() {
        final PublicKey source = usdcSource; // Private key's USDC token account
        final PublicKey destination = solDestinationExistToken;

        /*
            amount = "0.0001" usdc
            amount = 100
            decimals = 6
         */

        final long tokenAmount = 0;
        final byte decimals = 9;

        // Create account from private key
        final Account owner = testAccount;

        final String txId = tokenManager.transferCheckedToSolAddress(
                owner,
                source,
                destination,
                USDC_TOKEN_MINT,
                tokenAmount,
                decimals
        );

        System.out.println("txId:"+txId);
        // TODO - actually verify something
        assertNotNull(txId);
    }
}
