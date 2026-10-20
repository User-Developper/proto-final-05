package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "report")
@Table(name = "report_image")
public class ReportImage {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    private String fileName;
    private String contentType;
    private String storageName;
    private Long fileSize;
    private String storagePath;
    private LocalDateTime createdAt;
}
