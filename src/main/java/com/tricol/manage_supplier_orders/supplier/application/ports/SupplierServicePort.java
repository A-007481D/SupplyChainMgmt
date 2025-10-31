package com.tricol.manage_supplier_orders.supplier.application.ports;

import com.tricol.manage_supplier_orders.supplier.api.dto.SupplierResponseDTO;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierServicePort {
    Supplier create(CreateSupplierCommand cmd);
    Page<Supplier> list(Pageable pageable);
    Supplier getById(Long id);
    Supplier update(Long id, CreateSupplierCommand cmd);
    void delete(Long id);

    @Data
    final class CreateSupplierCommand {
        private final String company;
        private final String address;
        private final String contact;
        private final String email;
        private final String phone;
        private final String city;
        private final String ice;

        public CreateSupplierCommand(String company, String address, String contact, String email, String phone, String city, String ice) {
            this.company = company;
            this.address = address;
            this.contact = contact;
            this.email = email;
            this.phone = phone;
            this.city = city;
            this.ice = ice;
        }

        public String getCompany() { return company; }
        public String getAddress() { return address; }
        public String getContact() { return contact; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getCity() { return city; }
        public String getIce() { return ice; }
    }
}
