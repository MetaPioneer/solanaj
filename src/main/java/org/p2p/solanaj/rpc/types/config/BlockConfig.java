package org.p2p.solanaj.rpc.types.config;

import lombok.Setter;

@Setter
public class BlockConfig {

    private String encoding = "jsonParsed";

    private String transactionDetails = "full";

    private Boolean rewards = false;

    private String commitment;
}