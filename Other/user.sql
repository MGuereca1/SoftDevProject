USE employeedata;
CREATE TABLE Users(
    empid INT Primary Key AUTO_INCREMENT,
    Username VARCHAR(50) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

INSERT INTO Users (Username, Password, salt, role)
VALUES
('SBeagle', 'wkvdqDMfii58XhjrbPvGup+a/K8PTJrel750zPp0JAw=', 'UELls3oLOHv1D/aX2XmcZQ==', 'employee'),
('CBrown', 'gGNdMUZ5+QpBFM2Eia+3Kak4cFuyeoH+HlKoisOTKA0=','QKaTB/CidD8PYiE/I272Nw==', 'employee'),
('LDoctor', 'raDfAjd5Jg8da11rFPWw2DhZyANi8ht2iqVK6Z2DH2o=', 'ZGRCk+6VBpwEQZqDMJliZQ==', 'employee'),
('PPatti', 'xRcmvMs+Kdue6Pyd/sM2fI6rN4RDudfLjDhPBeJKhas=','lwv1PD+Ezugm9FMpiW0FMQ==','employee'),
('LBlanket', 'LUPcEr0ClyiR1BMq4Zvd8VuRUHK47UN/7mAC0F4hfHk=', '51vMcP8gV6aMQ4ao2GhcFw==', 'employee'),
('PDusty', '8l9sZQ/oeghchKDcLzZxPZH3vqJnV3Nj6mcaLuHxDG4=','UeOx98WW3QsiRldvp21PRw==', 'employee'),
('SDoo', 'eUkaE1+ktR76q0TLpxQLSNxlo9+BFK+NCNubWjZxvtI=', 'XDMJ38NfocM2Zdyt+YEKOg==', 'employee'),
('SRodgers', 'efekiA/R+L9qFiQh8SdZnn+V0GZUCCSLLSix4hmRWuQ=','MfyVeEoyERXgLiIgIp6Pcg==', 'employee'),
('VDinkley', 'GBdtE3TMHUm+aI9kIxiIJCkZd3LemmcWUYA1CDfVs6o=', 'DIZ2Cpg/p6ZFdeQ7/ZfDvA==', 'employee'),
('DBlake', '7JCNcBu0E2FBqe398bBZYYyUMYiOEszvH67P/zSILJ8=','rzhPEkS3oJhTSkAkdTdnYw==','employee'),
('BBunny', 'OyswYGWteo6HT3VG4Kt/L9pLQsDleiOE8Dus+JOyGvY=', 'b1HJSD02UVS/+uhvueAlaw==', 'employee'),
('DDuck', 'hDcopfAklcs6O+9xCWclPl9xwAOW9Q1fDLZf5x/COyQ=','U4TYXVZOi3A2q2S3w7u5+A==', 'employee'),
('PPig', 'C7eQvdvLYlLWs/WLdDfhds+8ODdhF3jOIBUNIJ5ywY4=', 'rWuPyXEbFsIVRXhafIpRlw==', 'employee'),
('EFudd', 'rGrywduL+zs8Bleoai0fGTN8TQqnx0WoIEQvFKZdWW0=', 'q+K7Zv92DhwLwOG6lIbVTA==', 'employee'),
('MMartian', 'vvs2sFnnBXpf0QGqmslXPpAmIdpkgVKkkSiow+oghCs=','WuSUGurlqMvm0FH6Ga67uQ==', 'employee')
('ADMIN', 'lEF028bAUsF0aPUD1s/duobHkoAwcWXMMS+YWQ6IJKY=','HXalfqgE3AjLJbemeyQ7bA==','admin');

