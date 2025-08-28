package com.bluesky.player.database.entity.audio;

import com.bluesky.player.database.entity.account.Account;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "audio")
@Data
public class Audio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "path", nullable = false)
    private String path;
    @ManyToOne()
    @JoinColumn(name = "publisher_id")
    private Account account;
    @ManyToOne()
    @JoinColumn(name = "play_list_id", nullable = true)
    private Playlist playList;
    @ManyToMany(mappedBy = "audios")
    private Set<Tag> tags = new HashSet<>();

}
