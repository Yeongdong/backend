package com.example.spinlog.user.entity;

import com.example.spinlog.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

@Entity
@Table(name = "budgets")
@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long id;

    @Min(0) @Max(100_000_000) //TODO validation 코드를 entity 에 넣어도 되는지
    @ColumnDefault("0")
    private Integer budget;

    @Column(updatable = false)
    private Integer year;

    @Column(updatable = false)
    private Integer month;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @Builder
    public Budget(Integer budget, Integer year, Integer month, User user) {
        this.budget = budget;
        this.year = year;
        this.month = month;
        this.user = user;
    }

    public void change(Integer budget) {
        this.budget = budget;
    }

    public boolean isMonthOf(LocalDate localDate) {
        return year == localDate.getYear() && month == localDate.getMonthValue();
    }
}
