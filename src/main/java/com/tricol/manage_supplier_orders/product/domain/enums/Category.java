package com.tricol.manage_supplier_orders.product.domain.enums;

import lombok.Getter;

@Getter
public enum Category {
    TEXTILE("Textile"),          // Fabrics, threads, and raw materials for clothing
    ACCESSORY("Accessory"),        // Buttons, zippers, labels, etc.
    EQUIPMENT("Equipment"),        // Machines or tools used in production
    PACKAGING("Packaging"),        // Boxes, wrapping, storage materials
    SAFETY_GEAR("Safety Gear"),      // Gloves, helmets, protective clothing
    MAINTENANCE("Maintenance"),      // Cleaning or maintenance supplies
    OTHER("Other");              // Miscellaneous items not categorized elsewhere

    private final String displayName;

    Category(String displayName) {
        this.displayName = this.name().charAt(0) + this.name().substring(1).toLowerCase().replace("_", " ");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
