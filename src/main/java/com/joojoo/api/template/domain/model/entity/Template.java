package com.joojoo.api.template.domain.model.entity;

import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.global.common.entity.BaseTimeEntity;
import com.joojoo.global.common.enums.TradeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "templates")
public class Template extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TradeType type;

    @Column(name = "title", nullable = false)
    private String title;

    // JSON 처리 (Hibernate 6 기준)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    private Map<String, Object> content;

    @Builder
    public Template(User user, TradeType type, String title, Map<String, Object> content) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.content = content;
    }
}
