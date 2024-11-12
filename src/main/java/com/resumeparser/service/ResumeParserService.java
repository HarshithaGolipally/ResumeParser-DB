package com.resumeparser.service;

import com.resumeparser.core.resume.parser.ResumeParserEngine;
import com.resumeparser.model.Resume;
import com.resumeparser.model.Skill;
import com.resumeparser.repository.EmployeeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;

import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ResumeParserService {

  private final ResumeParserEngine parserEngine;
  private static ExecutorService executors = Executors.newFixedThreadPool(2);
  private final ObjectMapper objectMapper;
  private final EmployeeRepository employeeRepository;

  public ResumeParserService(@Autowired final ResumeParserEngine pdfBoxResumeParserEngine, final ObjectMapper objectMapper, EmployeeRepository employeeRepository) {
    this.parserEngine = pdfBoxResumeParserEngine;
    this.objectMapper = objectMapper;
    this.employeeRepository = employeeRepository;
  }

  public Resume parse(final byte[] fileContent) {
    Resume resume = new Resume();
    try {
      List<Callable<Boolean>> dataPopulates = createDataPopulateCallables(resume, fileContent);
      executors.invokeAll(dataPopulates);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return resume;
  }

  private List<Callable<Boolean>> createDataPopulateCallables(final Resume resume, byte[] fileContent) {
    List<Callable<Boolean>> dataPopulates = new ArrayList<>();
    dataPopulates.add(() -> {
      populateAutomaticData(resume, fileContent);
      return true;
    });
    // dataPopulates.add(() -> {
    //   populateProfileImage(resume, fileContent);
    //   return true;
    // });
   return dataPopulates;
  }

  private void populateAutomaticData(final Resume resume, byte[] fileContent) {
    Try<JsonNode> jsonNodes = parserEngine.parseFile(fileContent);
    if (jsonNodes.isSuccess()) {
      JsonNode resultNode = jsonNodes.get();
      if (resultNode.has("title")) {
        JsonNode rootTitleNode = resultNode.get("title");
        if (rootTitleNode.has("probableName")) {
          String probableName = rootTitleNode.get("probableName").textValue();
          resume.setName(probableName);
        }
      }

      if (resultNode.has("email")) {
        JsonNode rootEmailNode = resultNode.get("email");
        if (rootEmailNode.has("email")) {
          String email = rootEmailNode.get("email").textValue();
          resume.setEmail(email);
        }
      }

      if (resultNode.has("phone_number")) {
        JsonNode rootPhoneNode = resultNode.get("phone_number");
        if (rootPhoneNode.has("phone")) {
          String phone = rootPhoneNode.get("phone").textValue();
          resume.setPhone(phone);
        }
      }

      if (resultNode.has("skills")) {
        JsonNode rootSkillsNode = resultNode.get("skills");
        List<Skill> skills = convertJsonNodeToSkillList(rootSkillsNode);

    // Set the resume reference in each Skill, if needed
    for (Skill skill : skills) {
        skill.setResume(resume);
    }

    // Set the list of skills in the Resume object
    resume.setSkills(skills);
      }
      employeeRepository.save(resume);
    }
  }
  private List<Skill> convertJsonNodeToSkillList(JsonNode rootSkillsNode) {
    List<Skill> skills = new ArrayList<>();

    if (rootSkillsNode.isArray()) { // Ensure it’s an array
        for (JsonNode skillNode : rootSkillsNode) {
            // Manually create a new Skill instance for each JsonNode
            Skill skill = new Skill();
            if (skillNode.has("skillName")) {
                skill.setSkillName(skillNode.get("skillName").asText()); // Set the skillName field
            }
            // Add other fields if needed, like setting a reference to the parent Resume object here if necessary
            skills.add(skill);
        }
    }
    return skills;
}

  // private void populateProfileImage(final Resume resume, final byte[] fileContent) {
  //   Try<byte[]> imageTry = parserEngine.extractImage(fileContent);
  //   if (!imageTry.isFailure()) {
  //     final String imageDataUri = "data:image/jpg;base64," +
  //         StringUtils.newStringUtf8(Base64.encodeBase64(imageTry.get(), false));
  //     resume.setImage(imageDataUri);
  //   }
  // }
}
