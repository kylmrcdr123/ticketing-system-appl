package com.rocs.ticketing.system.service.ticket;

import com.rocs.ticketing.system.domain.ticket.Ticket;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TicketService {
 List<Ticket> getAllTickets();
 Optional<Ticket> getTicketById(Long id);
 Ticket addTicket(Ticket ticket);
 void updateStatus(Long ticketId, String status);
 Ticket updateTicket(Ticket ticket);
 void assignTicket(Long ticketId, Long employeeId, Long studentId);
 void reassignTicket(Long ticketId, Long newMisStaffId);
 List<Ticket> getTicketByStatus(String status);
 List<Ticket> getTicketsByDateRange(Date dateCreated, Date dateFinished);
 List<Ticket> getAllTicketByMisStaffNumber(String misStaffNumber);
 boolean assignTicket(Long ticketId, Long misStaffId);
 List<Ticket> getAllTicketsByMisStaffName(String name);

 List<Ticket> getAllTicketByStaffId(Long staffId);

}
