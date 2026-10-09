package com.vsms.service;

import com.vsms.exception.ResourceNotFoundException;
import com.vsms.model.User;
import com.vsms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final UserRepository userRepository;

    public CustomerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllCustomers() {
        List<User> list = userRepository.findByRole("CUSTOMER");
        list.forEach(u -> u.setPassword(null));
        return list;
    }

    public User getCustomerById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        user.setPassword(null);
        return user;
    }

    public User updateCustomer(String id, User updated) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (updated.getName() != null && !updated.getName().isBlank()) {
            existing.setName(updated.getName().trim());
        }
        if (updated.getPhone() != null && !updated.getPhone().isBlank()) {
            existing.setPhone(updated.getPhone().trim());
        }
        User saved = userRepository.save(existing);
        saved.setPassword(null);
        return saved;
    }

    public void deleteCustomer(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public List<User> searchByName(String name) {
        List<User> list = userRepository.findByNameContainingIgnoreCase(name);
        list.forEach(u -> u.setPassword(null));
        return list;
    }
}
