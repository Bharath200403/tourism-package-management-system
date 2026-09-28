package com.tourism.service;

import com.tourism.dto.request.PackageScheduleRequest;
import com.tourism.dto.response.PackageScheduleResponseDTO;
import com.tourism.entity.PackageSchedule;
import com.tourism.entity.TourPackage;
import com.tourism.entity.enums.ScheduleStatus;
import com.tourism.exception.BadRequestException;
import com.tourism.exception.ResourceNotFoundException;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.PackageScheduleRepository;
import com.tourism.repository.TourPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final PackageScheduleRepository scheduleRepository;
    private final TourPackageRepository packageRepository;
    private final AuditService auditService;

    public List<PackageScheduleResponseDTO> listForPackage(Long packageId) {
        return scheduleRepository.findByTourPackageId(packageId).stream()
                .map(DtoMapper::toScheduleDTO).toList();
    }

    @Transactional
    public PackageScheduleResponseDTO create(Long packageId, PackageScheduleRequest request) {
        TourPackage p = packageRepository.findById(packageId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found: " + packageId));

        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new BadRequestException("Start date must be before end date.");
        }
        if (request.getCapacity() <= 0) {
            throw new BadRequestException("Capacity must be greater than zero.");
        }

        PackageSchedule s = new PackageSchedule();
        s.setTourPackage(p);
        s.setStartDate(request.getStartDate());
        s.setEndDate(request.getEndDate());
        s.setCapacity(request.getCapacity());
        s.setAvailableSeats(request.getCapacity());
        s.setStatus(request.getStartDate().isBefore(LocalDate.now()) ? ScheduleStatus.CLOSED : ScheduleStatus.OPEN);
        scheduleRepository.save(s);

        auditService.log("SCHEDULE_CREATED", "PackageSchedule", String.valueOf(s.getId()),
                "Package " + packageId + " schedule " + s.getStartDate() + " - " + s.getEndDate());
        return DtoMapper.toScheduleDTO(s);
    }

    @Transactional
    public PackageScheduleResponseDTO update(Long scheduleId, PackageScheduleRequest request) {
        PackageSchedule s = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + scheduleId));

        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new BadRequestException("Start date must be before end date.");
        }
        int seatsBooked = s.getCapacity() - s.getAvailableSeats();
        if (request.getCapacity() < seatsBooked) {
            throw new BadRequestException("Capacity cannot be reduced below the number of seats already booked (" + seatsBooked + ").");
        }

        s.setStartDate(request.getStartDate());
        s.setEndDate(request.getEndDate());
        s.setAvailableSeats(s.getAvailableSeats() + (request.getCapacity() - s.getCapacity()));
        s.setCapacity(request.getCapacity());
        scheduleRepository.save(s);

        auditService.log("SCHEDULE_UPDATED", "PackageSchedule", String.valueOf(scheduleId), "Updated schedule");
        return DtoMapper.toScheduleDTO(s);
    }

    @Transactional
    public void setStatus(Long scheduleId, ScheduleStatus status) {
        PackageSchedule s = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + scheduleId));
        s.setStatus(status);
        scheduleRepository.save(s);
        auditService.log("SCHEDULE_STATUS_CHANGED", "PackageSchedule", String.valueOf(scheduleId), "New status: " + status);
    }

    /** Auto-closes schedules whose start date has passed. Invoked by a scheduled job. */
    @Transactional
    public void closeExpiredSchedules() {
        List<PackageSchedule> expired = scheduleRepository.findByStartDateBeforeAndStatus(LocalDate.now(), ScheduleStatus.OPEN);
        for (PackageSchedule s : expired) {
            s.setStatus(ScheduleStatus.CLOSED);
        }
        scheduleRepository.saveAll(expired);
    }
}
