package com.rocs.ticketing.system.repository.ticket;

import com.rocs.ticketing.system.domain.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByTicketNumber(String ticketNumber);
    List<Ticket> findByTicketId(Long ticketId);
    List<Ticket> findByStatus(String status);
    List<Ticket> findByStatusIgnoreCase(String status);

    @Query("SELECT t FROM Ticket t WHERE t.dateCreated BETWEEN :dateCreated AND :dateFinished")
    List<Ticket> findByDateRangeBetween(
            @Param("dateCreated") Date dateCreated,
            @Param("dateFinished") Date dateFinished);


    List<Ticket> findByMisStaff_MisStaffNumber(String misStaffNumber);

    List<Ticket> findByMisStaff_StaffId(Long staffId);

    List<Ticket> findByMisStaffFirstNameContainingIgnoreCaseOrMisStaffMiddleNameContainingIgnoreCaseOrMisStaffLastNameContainingIgnoreCase(String firstName, String middleName, String lastName);
    List<Ticket> findByStudent_StudentId(Long Id);
    List<Ticket> findByEmployee_EmployeeId(Long Id);
    List<Ticket> findByMisStaff_Id(Long Id);
    List<Ticket> findByEmployee_EmployeeNumber(String employeeNumber);
    List<Ticket> findByStudent_StudentNumber(String studentNumber);
}
