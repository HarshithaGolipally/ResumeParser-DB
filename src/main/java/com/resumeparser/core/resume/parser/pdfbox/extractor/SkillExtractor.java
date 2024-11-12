package com.resumeparser.core.resume.parser.pdfbox.extractor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Extracts skills from a resume PDF document.
 */
@Component
public class SkillExtractor implements Extractor {
    

  //private static final Logger LOGGER = Logger.getLogger(SkillExtractor.class.getName());

  // Define a list of common skills to search for. You could load this from a file or database if needed.
  private final Set<String> skillset; 

  public SkillExtractor(@Value("${resume.skills}") List<String> skills) {
    this.skillset = new HashSet<>(skills);
  }

  @Override
  public Optional<JsonNode> extract(PDDocument document) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode result = objectMapper.createObjectNode();
      //System.out.println("I am here");

      // Extract text from the entire PDF
      PDFTextStripper textStripper = new PDFTextStripper();
      textStripper.setEndPage(1);
      String fullText = textStripper.getText(document);

      // Extract skills from the text
      Set<String> extractedSkills = extractSkillsFromText(fullText);
      //System.out.println(extractedSkills);

      // If any skills are found, add them to the JSON result
      if (!extractedSkills.isEmpty()) {
        ArrayNode skillsArray = objectMapper.valueToTree(extractedSkills);
        result.set("skills", skillsArray);
        //System.out.println(skillsArray);
        return Optional.of(result);
      } else {
       // LOGGER.info("No skills found in the resume.");
        return Optional.empty();
      }

    } catch (IOException e) {
      //LOGGER.severe("Error reading PDF document: " + e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Checks the resume text for any skills listed in the predefined skill set.
   *
   * @param text The extracted text from the PDF document.
   * @return A set of skills found in the text.
   */
  private Set<String> extractSkillsFromText(String text) {
    Set<String> foundSkills = new HashSet<>();

    // Convert text to lowercase to make skill matching case-insensitive
    String lowerCaseText = text.toLowerCase();

    for (String skill : skillset) {
        if (lowerCaseText.contains(skill.toLowerCase())) {
          foundSkills.add(skill);
        }
      }

    return foundSkills;
  }

  @Override
  public String getKeyName() {
    return "skills";
  }
}
