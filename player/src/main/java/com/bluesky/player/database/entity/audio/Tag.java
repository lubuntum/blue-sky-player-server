package com.bluesky.player.database.entity.audio;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "playlist")
@Data
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, length = 25, nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "audio_tag",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "audio_tag")
    )
    private Set<Audio> audios = new HashSet<>();
}
