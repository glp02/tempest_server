package com.sky.tempest_server.weather.entities;

        import lombok.AllArgsConstructor;
        import lombok.Data;
        import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateValue {
    private String date;
    private double value;
}
