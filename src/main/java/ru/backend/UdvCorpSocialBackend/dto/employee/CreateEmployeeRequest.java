package ru.backend.UdvCorpSocialBackend.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.Department;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.LegalEntity;
import ru.backend.UdvCorpSocialBackend.model.Subdivision;
import ru.backend.UdvCorpSocialBackend.model.RoleType;
import ru.backend.UdvCorpSocialBackend.model.WorkStatus;

@Getter
@Setter
public class CreateEmployeeRequest {

    @NotBlank(message = "Полное имя обязательно")
    @Size(max = 255, message = "Полное имя не должно превышать 255 символов")
    private String fullName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Size(max = 255, message = "Email не должен превышать 255 символов")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, max = 255, message = "Пароль должен быть от 8 до 255 символов")
    private String password;

    private RoleType role;

    private WorkStatus workStatus;

    @Size(max = 100, message = "Должность не должна превышать 100 символов")
    private String position;

    private Department department;

    private Subdivision subdivision;

    private LegalEntity legalEntity;

    @Size(max = 100, message = "Место работы не должно превышать 100 символов")
    private String workplace;

    @Size(max = 100, message = "Telegram не должен превышать 100 символов")
    private String telegram;

    @Size(max = 100, message = "Mattermost не должен превышать 100 символов")
    private String mattermost;

    private Employee supervisor;

    @Size(max = 50, message = "Уровень профиля не должен превышать 50 символов")
    private String profileLevel;
}
