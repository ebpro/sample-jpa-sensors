package fr.univtln.bruno.samples.jpa.sensors.observations;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

@Getter
@ToString
@NoArgsConstructor

@Embeddable
public class Result {
    @Column(name = "MEASURE_VALUE")
    private Number value;
    @Column(name = "MEASURE_UNIT")
    private String unit;

    private Result(Quantity quantity) {
        this.value = quantity.getValue();
        this.unit = quantity.getUnit().toString();
    }

    public static Result of(String result) {
        return new Result(Quantities.getQuantity(result));
    }

    public static Result of(Number value, Unit unit) {
        return new Result(Quantities.getQuantity(value, unit));
    }

    public static Result of(Quantity quantity) {
        return new Result(quantity);
    }
}
