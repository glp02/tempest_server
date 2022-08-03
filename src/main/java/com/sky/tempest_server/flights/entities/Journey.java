package com.sky.tempest_server.flights.entities;

import com.sky.tempest_server.user.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Journey implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true,nullable = false,name = "journey_id" , updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false,name = "user_id" , updatable = false,referencedColumnName = "user_id")
    private User user;

    @Column(nullable = false,name = "journey_name")
    private String name;

    @ManyToOne
    @JoinColumn(nullable = false,name = "outbound_flight",referencedColumnName = "flight_id")
    private Flight outboundFlight;

    @ManyToOne
    @JoinColumn(name="return_flight",referencedColumnName = "flight_id" )
    private Flight returnFlight;

    public Journey(User user, String name, Flight outboundFlight, Flight returnFlight){
        setUser(user);
        setName(name);
        setOutboundFlight(outboundFlight);
        setReturnFlight(returnFlight);
    }

    /*public JourneyDTO dto(){
        return new JourneyDTO(getId(),getUser().getEmail(),getName(),getOutboundFlight(),getReturnFlight());
    }*/
}