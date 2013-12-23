OPF Project Healthcheck
=======================

About
-----
Java project that uses the GitHub API to gather information about OPF GitHub projects
and generate a static web site that:

 * Summarises the health of the projects based upon OPF guidelines
 * Provides sub pages for more detailed infromation

The project is aimed to be run as a scheduled task and the results made public via a
server.  To set it up in this fashion you'd require a web server and a little
configuration, a sysadmin task.

Status
------
OPF Jenkins Build Status[![Build Status](http://jenkins.opf-labs.org/job/project-healthcheck/badge/icon)](http://jenkins.opf-labs.org/job/project-healthcheck/)

ToDo
----
- [x] First cut that generates the home page summary
- [ ] Instructions to generate as a local file and browse in this README
