package com.tourism.service;

import com.tourism.dto.request.DestinationRequest;
import com.tourism.dto.response.DestinationResponseDTO;
import com.tourism.dto.response.PageResponseDTO;
import com.tourism.entity.Destination;
import com.tourism.entity.enums.PackageStatus;
import com.tourism.exception.BadRequestException;
import com.tourism.exception.ResourceNotFoundException;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final AuditService auditService;

    public PageResponseDTO<DestinationResponseDTO> list(PackageStatus status, Pageable pageable) {
        Page<Destination> page = status != null
                ? destinationRepository.findByStatus(status, pageable)
                : destinationRepository.findAll(pageable);
        return PageResponseDTO.of(page.map(DtoMapper::toDestinationDTO));
    }

    public DestinationResponseDTO getById(Long id) {
        return DtoMapper.toDestinationDTO(getOrThrow(id));
    }

    @Transactional
    public DestinationResponseDTO create(DestinationRequest request) {
        Destination d = new Destination();
        apply(d, request);
        destinationRepository.save(d);
        auditService.log("DESTINATION_CREATED", "Destination", String.valueOf(d.getId()), d.getName());
        return DtoMapper.toDestinationDTO(d);
    }

    @Transactional
    public DestinationResponseDTO update(Long id, DestinationRequest request) {
        Destination d = getOrThrow(id);
        apply(d, request);
        destinationRepository.save(d);
        auditService.log("DESTINATION_UPDATED", "Destination", String.valueOf(d.getId()), d.getName());
        return DtoMapper.toDestinationDTO(d);
    }

    @Transactional
    public void deactivate(Long id) {
        Destination d = getOrThrow(id);
        d.setStatus(PackageStatus.INACTIVE);
        destinationRepository.save(d);
        auditService.log("DESTINATION_DEACTIVATED", "Destination", String.valueOf(id), d.getName());
    }

    private void apply(Destination d, DestinationRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Destination name is required.");
        }
        d.setName(request.getName());
        d.setState(request.getState());
        d.setCountry(request.getCountry());
        d.setDescription(request.getDescription());
        d.setBestSeason(request.getBestSeason());
        d.setEstimatedDuration(request.getEstimatedDuration());
        if (request.getStatus() != null) {
            d.setStatus(PackageStatus.valueOf(request.getStatus().toUpperCase()));
        } else if (d.getStatus() == null) {
            d.setStatus(PackageStatus.ACTIVE);
        }
    }

    private Destination getOrThrow(Long id) {
        return destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: " + id));
    }
}
