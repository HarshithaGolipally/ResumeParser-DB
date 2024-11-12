package com.resumeparser.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

//import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.Data;

@Entity
@Table(name = "employees")
@Data
public class Resume {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long empId;
 
  private String name;
  private String email;
  private String phone;

  @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Skill> skills;
  //private String image;

  // @Override
  // public String toString() {
  //   return "Resume [name=" + name + ", email=" + email + ", phone=" + phone + ", skills=" + skills + "]";
  // }

  
}
