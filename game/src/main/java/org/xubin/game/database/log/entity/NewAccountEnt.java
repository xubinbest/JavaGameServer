package org.xubin.game.database.log.entity;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Setter
@Table(name = "new_account")
public class NewAccountEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "create_time")
    private long createTime;
}
