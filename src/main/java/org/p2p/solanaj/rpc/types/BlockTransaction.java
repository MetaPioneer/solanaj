package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;

import java.util.List;

@Getter
@ToString
public class BlockTransaction {

    @Getter
    @ToString
    public static class Data {

        @Json(name = "info")
        private Object info;

        @Json(name = "type")
        private String type;
    }

    @Getter
    @ToString
    public static class Instruction {

        @Json(name = "parsed")
        private Object parsed;

        @Json(name = "program")
        private String program;

        @Json(name = "programId")
        private String programId;
    }

    @Getter
    @ToString
    public static class AccountMeta {

        private String pubkey;

        private boolean signer;

        private boolean writable;
    }

    @Getter
    @ToString
    public static class Message {

        @Json(name = "accountKeys")
        private List<AccountMeta> accountKeys;

        @Json(name = "instructions")
        private List<Instruction> instructions;

        @Json(name = "recentBlockhash")
        private String recentBlockhash;
    }

    @Getter
    @ToString
    public static class Status {

        @Json(name = "Ok")
        private Object ok;
    }

    @Getter
    @ToString
    public static class TokenBalance {

        @Json(name = "accountIndex")
        private Double accountIndex;

        @Json(name = "mint")
        private String mint;

        @Json(name = "uiTokenAmount")
        private TokenResultObjects.TokenAmountInfo uiTokenAmount;
    }

    @Getter
    @ToString
    public static class Meta {

        @Json(name = "err")
        private Object err;

        @Json(name = "fee")
        private long fee;

        @Json(name = "innerInstructions")
        private List<Object> innerInstructions;

        @Json(name = "preTokenBalances")
        private List<TokenBalance> preTokenBalances;

        @Json(name = "postTokenBalances")
        private List<TokenBalance> postTokenBalances;

        @Json(name = "postBalances")
        private List<Long> postBalances;

        @Json(name = "preBalances")
        private List<Long> preBalances;

        @Json(name = "status")
        private Status status;
    }

    @Getter
    @ToString
    public static class Transaction {

        @Json(name = "message")
        private Message message;

        @Json(name = "signatures")
        private List<String> signatures;
    }

    @Json(name = "blockTime")
    private long blockTime;

    @Json(name = "meta")
    private Meta meta;

    @Json(name = "slot")
    private long slot;

    @Json(name = "transaction")
    private Transaction transaction;
}
