package onetoone.Access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * This class gets Access to the Join Table from the many to many relation
 */
@Service
public class FileAccess{

    @Autowired
    private AccessRepository access;

    public List<String> getFileNamesByAccessId(Long accessId) {
        return access.findFileNamesByAccessId(accessId);
    }
}
