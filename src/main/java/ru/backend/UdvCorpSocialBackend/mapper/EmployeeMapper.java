package ru.backend.UdvCorpSocialBackend.mapper;

import org.springframework.stereotype.Component;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeProfileDto;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeProfileResponseDto;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeResponseDto;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.EmployeeProfile;

import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    public EmployeeResponseDto toResponseDto(Employee employee) {
        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setId(employee.getId());
        dto.setFullName(employee.getFullName());
        dto.setPhotoUrl(employee.getPhotoUrl());
        dto.setPosition(employee.getPosition());
        dto.setEmail(employee.getEmail());
        dto.setOnlineStatus(employee.getOnlineStatus());
        dto.setWorkStatus(employee.getWorkStatus());
        dto.setWorkplace(employee.getWorkplace());
        dto.setTelegram(employee.getTelegram());
        dto.setMattermost(employee.getMattermost());
        dto.setRole(employee.getRole());
        dto.setProfileLevel(employee.getProfileLevel());

        // Устанавливаем ID для связанных сущностей
        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getId());
        }
        if (employee.getSubdivision() != null) {
            dto.setSubdivisionId(employee.getSubdivision().getId());
        }
        if (employee.getLegalEntity() != null) {
            dto.setLegalEntityId(employee.getLegalEntity().getId());
        }
        if (employee.getSupervisor() != null) {
            dto.setSupervisorId(employee.getSupervisor().getId());
        }

        // Преобразование EmployeeProfile
        EmployeeProfile profile = employee.getProfile();
        if (profile != null) {
            EmployeeProfileResponseDto profileDto = new EmployeeProfileResponseDto();
            profileDto.setEmployeeId(profile.getEmployeeId());
            profileDto.setStatusState(profile.getStatusState());
            profileDto.setStatusComment(profile.getStatusComment());
            profileDto.setAboutMe(profile.getAboutMe());
            profileDto.setBirthday(profile.getBirthday());
            profileDto.setCity(profile.getCity());
            profileDto.setHobbies(profile.getHobbies());
            profileDto.setEmploymentStatus(profile.getEmploymentStatus());
            dto.setProfile(profileDto);
        }

        return dto;
    }

    public EmployeeProfileDto toProfileDto(Employee employee) {
        EmployeeProfileDto dto = new EmployeeProfileDto();
        dto.setId(employee.getId());
        dto.setFullName(employee.getFullName());
        dto.setPhotoUrl(employee.getPhotoUrl());
        dto.setPosition(employee.getPosition());
        dto.setDepartmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null);
        dto.setSubdivisionName(employee.getSubdivision() != null ? employee.getSubdivision().getName() : null);
        dto.setMattermost(employee.getMattermost());
        dto.setTelegram(employee.getTelegram());
        dto.setEmail(employee.getEmail());
        dto.setOnlineStatus(employee.getOnlineStatus());
        dto.setWorkStatus(employee.getWorkStatus());
        dto.setSupervisorName(employee.getSupervisor() != null ? employee.getSupervisor().getFullName() : null);
        dto.setLegalEntityName(employee.getLegalEntity() != null ? employee.getLegalEntity().getName() : null);
        dto.setWorkplace(employee.getWorkplace());
        dto.setProfileLevel(employee.getProfileLevel());

        EmployeeProfile profile = employee.getProfile();
        if (profile != null) {
            dto.setStatusState(profile.getStatusState());
            dto.setStatusComment(profile.getStatusComment());
            dto.setAboutMe(profile.getAboutMe());
            dto.setBirthday(profile.getBirthday());
            dto.setCity(profile.getCity());
            dto.setHobbies(profile.getHobbies());
            dto.setEmploymentStatus(profile.getEmploymentStatus());
        }

        dto.setProjectNames(employee.getProjects().stream()
                .map(project -> project.getName())
                .collect(Collectors.toSet()));

        return dto;
    }
}
