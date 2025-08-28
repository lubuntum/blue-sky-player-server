package com.bluesky.player.database.entity.audio;

import com.bluesky.player.database.entity.account.Account;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "playlist")
@Data
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne()
    @JoinColumn(name = "creator_id")
    private Account account;
}
