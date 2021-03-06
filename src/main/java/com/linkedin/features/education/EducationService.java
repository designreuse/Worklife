package com.linkedin.features.education;

import com.linkedin.config.errors.NotAuthorizedException;
import com.linkedin.config.errors.ObjectNotFoundException;
import com.linkedin.config.security.AuthenticationFacade;
import com.linkedin.entities.Education;
import com.linkedin.entities.Job;
import com.linkedin.entities.Login;
import com.linkedin.entities.repo.EducationRepository;
import com.linkedin.features.users.UserConverter;
import com.linkedin.model.education.EducationDto;
import com.linkedin.model.education.EducationRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EducationService {
  private EducationRepository educationRepository;
  private EducationConverter educationConverter;


  @Autowired
  public EducationService(EducationRepository educationRepository, UserConverter userConverter, EducationConverter educationConverter) {
    this.educationRepository = educationRepository;
    this.educationConverter = educationConverter;
  }

  //returns list of Education for a the specific User
  public List<EducationDto> getUsersEducation() {
    Login login = AuthenticationFacade.authenticatedUser();

    Long userId = login.getUserId();
    return educationRepository.findByUserId(userId)
        .stream()
        .map(educationConverter::toEducationDto)
        .collect(Collectors.toList());
  }


  public List<EducationDto> getUsersEducation(Long userId) {

    return educationRepository.findByUserId(userId)
        .stream()
        .map(educationConverter::toEducationDto)
        .collect(Collectors.toList());
  }

  public Education createEducation(EducationRequestDto educationRequestDto) {
    Login login = AuthenticationFacade.authenticatedUser();
    Long userId = login.getUserId();

    Education education = new Education();
    education.setUniversityName(educationRequestDto.getUniversityName());
    education.setUniversityDegree(educationRequestDto.getUniversityDegree());
    education.setUserId(userId);
    education.setEndDate(educationRequestDto.getEndDate());
    education.setStartDate(educationRequestDto.getStartDate());
    education.setVisible(educationRequestDto.getVisible());


    educationRepository.save(education);
    return education;
  }


  public Education changeEducation(EducationRequestDto educationRequestDto, Long educationId) throws Exception {


    if (!educationRepository.existsById(educationId)) {
      throw new ObjectNotFoundException(Education.class, educationId);
    }


    Login login = AuthenticationFacade.authenticatedUser();
    Long userId = login.getUserId();

    //we check if the user that is  not changing anothers user  Education
    Education educationCheck = educationRepository.findById(educationId).orElse(null);
    if (!userId.equals(educationCheck != null ? educationCheck.getUserId() : null)) {
      throw new NotAuthorizedException(Job.class);
    }

    Education education = new Education();
    education.setEducationId(educationId);
    education.setUniversityName(educationRequestDto.getUniversityName());
    education.setUniversityDegree(educationRequestDto.getUniversityDegree());
    education.setUserId(userId);
    education.setEndDate(educationRequestDto.getEndDate());
    education.setStartDate(educationRequestDto.getStartDate());
    education.setVisible(educationRequestDto.getVisible());

    educationRepository.save(education);
    return education;
  }

  public void removeEducation(Long educationId) throws Exception {
    if (educationRepository.existsById(educationId)) {
      Login login = AuthenticationFacade.authenticatedUser();
      Long userId = login.getUserId();
      Education education = educationRepository.findById(educationId).orElse(null);

      if (userId.equals(education != null ? education.getUserId() : null)) {
        throw new NotAuthorizedException(Education.class);
      }

      educationRepository.delete(education);
    } else {
      throw new ObjectNotFoundException(Education.class, educationId);
    }

  }

}
