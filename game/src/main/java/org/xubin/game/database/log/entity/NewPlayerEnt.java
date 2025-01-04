package org.xubin.game.database.log.entity;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Setter
@Table(name = "new_player")
public class NewPlayerEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "create_time")
    private long createTime;
}
