# Resume Parser
Resume Parser is a REST API based service to parse basic informations from resume pdf.

## Run Project
### Parse a Resume
#### Request
To parse a resume need to follow this cURL request

```Shell
curl -XPOST -F file=@resume.pdf http://localhost:8080/resume/parse
```
#### Response
```json 
{
  "name": "Name from resume",
  "email":"email@email.com",
  "phone":"+8801XXXXXXXXX",
  "skills": [
"Spring",
"Machine Learning",
"React",
"Angular",
"SQL",
"Python",
"Communication"
]
}
```
