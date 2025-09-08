package org.example.gridgestagram.repository.files;

import org.example.gridgestagram.repository.files.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository extends JpaRepository<Files, Long> {

}
