package com.zerotrust.audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private AuditRepository repository;

    public void save(AuditLog log) {
        // Uniquement des INSERT — jamais UPDATE ni DELETE
        repository.save(log);
    }

    public List<AuditLog> getAll() {
        return repository.findAllByOrderByTimestampDesc();
    }
}