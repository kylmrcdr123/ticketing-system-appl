package com.rocs.ticketing.system.repository.ticket;

import com.rocs.ticketing.system.domain.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByTicketNumber(String ticketNumber);
    List<Ticket> findByStatus(String status);
    List<Ticket> findByStatusIgnoreCase(String status);

    @Query("SELECT t FROM Ticket t WHERE t.dateCreated BETWEEN :dateCreated AND :dateFinished")
    List<Ticket> findByDateRangeBetween(@Param("dateCreated") Date dateCreated, @Param("dateFinished") Date dateFinished);

    List<Ticket> findByMisStaff_MisStaffNumber(String misStaffNumber);
    List<Ticket> findByEmployees_EmployeeNumber(String employeeNumber);
    List<Ticket> findByStudents_StudentNumber(String studentNumber);

    List<Ticket> findByMisStaffFirstNameContainingIgnoreCaseOrMisStaffMiddleNameContainingIgnoreCaseOrMisStaffLastNameContainingIgnoreCase(String firstName, String middleName, String lastName);

}
