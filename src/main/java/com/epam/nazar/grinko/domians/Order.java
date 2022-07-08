package com.epam.nazar.grinko.domians;

import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "orders")
@Accessors(chain = true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    @OneToOne(optional = false, mappedBy = "order", orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Bill bill;

    @OneToOne(optional = false, mappedBy = "order", orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Breakdown breakdown;

    @OneToOne(optional = false, mappedBy = "order", orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cancellation cancellation;

}
