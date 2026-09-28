package com.tourism.service;

import com.tourism.dto.request.SupportTicketRequest;
import com.tourism.dto.request.SupportTicketUpdateRequest;
import com.tourism.dto.response.SupportTicketResponseDTO;
import com.tourism.entity.SupportTicket;
import com.tourism.entity.User;
import com.tourism.entity.enums.TicketStatus;
import com.tourism.exception.ResourceNotFoundException;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final AuditService auditService;

    @Transactional
    public SupportTicketResponseDTO create(User customer, SupportTicketRequest request) {
        SupportTicket ticket = new SupportTicket();
        ticket.setCustomer(customer);
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(TicketStatus.OPEN);
        ticketRepository.save(ticket);
        return DtoMapper.toTicketDTO(ticket);
    }

    public List<SupportTicketResponseDTO> mine(Long customerId) {
        return ticketRepository.findByCustomerId(customerId).stream().map(DtoMapper::toTicketDTO).toList();
    }

    public List<SupportTicketResponseDTO> all() {
        return ticketRepository.findAll().stream().map(DtoMapper::toTicketDTO).toList();
    }

    @Transactional
    public SupportTicketResponseDTO update(Long ticketId, SupportTicketUpdateRequest request) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
        ticket.setStatus(TicketStatus.valueOf(request.getStatus().toUpperCase()));
        if (request.getResolutionNotes() != null) {
            ticket.setResolutionNotes(request.getResolutionNotes());
        }
        ticketRepository.save(ticket);
        auditService.log("SUPPORT_TICKET_UPDATED", "SupportTicket", String.valueOf(ticketId), "Status: " + ticket.getStatus());
        return DtoMapper.toTicketDTO(ticket);
    }
}
