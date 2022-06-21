package com.epam.nazar.grinko.domians;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "managers_decisions")
@Getter
@Setter
@Accessors(chain = true)
public class ManagerDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "manager_id")
    private User manager;

}
