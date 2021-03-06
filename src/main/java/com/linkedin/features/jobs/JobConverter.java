package com.linkedin.features.jobs;

import com.linkedin.entities.Job;
import com.linkedin.entities.repo.UserRepository;
import com.linkedin.features.users.UserConverter;
import com.linkedin.model.jobs.JobDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobConverter {

  private final UserRepository userRepository;
  private final UserConverter userConverter;

  @Autowired
  public JobConverter(UserRepository userRepository, UserConverter userConverter) {
    this.userRepository = userRepository;
    this.userConverter = userConverter;
  }

  public JobDto toJobDto(Job job) {
    JobDto dto = new JobDto();
    dto.setId(job.getJobId());
    dto.setTitle(job.getTitle());
    dto.setCompany(job.getCompany());
    dto.setAuthor(userConverter.toUserSimpleDto(job.getAuthorId()));
    dto.setDescription(job.getDescription());
    dto.setDateCreated(job.getDate());
    dto.setSkills(job.getSkills());
    return dto;
  }


}
