--
-- Copyright (c) 2004-2018, University of Oslo
-- All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without
-- modification, are permitted provided that the following conditions are met:
-- Redistributions of source code must retain the above copyright notice, this
-- list of conditions and the following disclaimer.
--
-- Redistributions in binary form must reproduce the above copyright notice,
-- this list of conditions and the following disclaimer in the documentation
-- and/or other materials provided with the distribution.
-- Neither the name of the HISP project nor the names of its contributors may
-- be used to endorse or promote products derived from this software without
-- specific prior written permission.
--
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
-- ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
-- WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
-- DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
-- ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
-- (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
-- LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
-- ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
-- (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
-- SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--

-- @formatter:off

-- Gender Constants (Adapter Gender Code to DHIS2 code as value)
INSERT INTO fhir_constant (id, version, category, name, code, data_type, value)
VALUES ('fa4a3a0eca4640e4b8323aec96bed55e', 0, 'GENDER', 'Gender Female', 'GENDER_FEMALE', 'STRING', 'Female');
INSERT INTO fhir_constant (id, version, category, name, code, data_type, value)
VALUES ('1ded2081883643dda5e17cb9562c93ef', 0, 'GENDER', 'Gender Male', 'GENDER_MALE', 'STRING', 'Male');

INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('9e1c64d882df40e5927ba34c115f14e9', 0, 'Organization Unit', 'ORGANIZATION_UNIT', 'Organization units that exists on DHIS2.');
INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('91065580f4fa4135969a498e9518c0c8', 0, 'Role Code', 'ROLE_CODE', 'Personal relationship role type.');

INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('3ffa1fe58ded4f18afa6d847d2b94217', 0, 'FHIR Role Code V2', 'SYSTEM_FHIR_ROLE_CODE_V2', 'http://hl7.org/fhir/v2/0131',
'FHIR personal relationship role type V2.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('64d3e785d6a54be59bc95ed9b938e2f6', 0, 'FHIR Role Code V3', 'SYSTEM_FHIR_ROLE_CODE_V3', 'http://hl7.org/fhir/v3/RoleCode',
'FHIR personal relationship role type V3.');

INSERT INTO fhir_code(id, version, code_category_id, name, code) VALUES ('aa36eeb633f14637936ac934abdaf83f', 0, '91065580f4fa4135969a498e9518c0c8', 'RC Contact Person', 'RC_CP');
INSERT INTO fhir_code(id, version, code_category_id, name, code) VALUES ('774ee335a7034753b524da49e4f10454', 0, '91065580f4fa4135969a498e9518c0c8', 'RC Emergency Contact', 'RC_C');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4611966d6eea4277bfcea515ac879ef7', 0, '91065580f4fa4135969a498e9518c0c8', 'RC family member', 'RC_FAMMEMB', 'A relationship between two people characterizing their "familial" relationship');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8ac10593daeb4885ade6abbd89d1d089', 0, '91065580f4fa4135969a498e9518c0c8', 'RC child', 'RC_CHILD', 'The player of the role is a child of the scoping entity.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e0cf031a971748c78f64dd21a2dea323', 0, '91065580f4fa4135969a498e9518c0c8', 'RC adopted child', 'RC_CHLDADOPT', 'The player of the role is a child taken into a family through legal means and raised by the scoping person (parent) as his or her own child.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('0836ecaa413f450f9222c648dfbe2768', 0, '91065580f4fa4135969a498e9518c0c8', 'RC adopted daughter', 'RC_DAUADOPT', 'The player of the role is a female child taken into a family through legal means and raised by the scoping person (parent) as his or her own child.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('2ea8b30db4684f03a3fb26cdc1531022', 0, '91065580f4fa4135969a498e9518c0c8', 'RC adopted son', 'RC_SONADOPT', 'The player of the role is a male child taken into a family through legal means and raised by the scoping person (parent) as his or her own child.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('27521b84cdb44ee0ae4eb2d6c982fbb0', 0, '91065580f4fa4135969a498e9518c0c8', 'RC foster child', 'RC_CHLDFOST', 'The player of the role is a child receiving parental care and nurture from the scoping person (parent) but not related to him or her through legal or blood ties.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('5b52db2e9b04464ea6d63b4e1bf8760e', 0, '91065580f4fa4135969a498e9518c0c8', 'RC foster daughter', 'RC_DAUFOST', 'The player of the role is a female child receiving parental care and nurture from the scoping person (parent) but not related to him or her through legal or blood ties.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('033f40ad54d549ad991b5d6f52e42dd8', 0, '91065580f4fa4135969a498e9518c0c8', 'RC foster son', 'RC_SONFOST', 'The player of the role is a male child receiving parental care and nurture from the scoping person (parent) but not related to him or her through legal or blood ties.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('26a2a3329b52426383fab4cf4e377487', 0, '91065580f4fa4135969a498e9518c0c8', 'RC daughter', 'RC_DAUC', 'Description: The player of the role is a female child (of any type) of scoping entity (parent)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('2e16153899d349f1b83a1569d7652fc7', 0, '91065580f4fa4135969a498e9518c0c8', 'RC natural daughter', 'RC_DAU', 'The player of the role is a female offspring of the scoping entity (parent).');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f8cf5e93551b4da5b4f1e6773807faaf', 0, '91065580f4fa4135969a498e9518c0c8', 'RC stepdaughter', 'RC_STPDAU', 'The player of the role is a daughter of the scoping person''s spouse by a previous union.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f1891d46cd3644afa349b7f542bb4e41', 0, '91065580f4fa4135969a498e9518c0c8', 'RC natural child', 'RC_NCHILD', 'The player of the role is an offspring of the scoping entity as determined by birth.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('81f449f6b875403f96b17d5fe6b16152', 0, '91065580f4fa4135969a498e9518c0c8', 'RC natural son', 'RC_SON', 'The player of the role is a male offspring of the scoping entity (parent).');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('6b262cf27738463f8a7c6b8ef4502871', 0, '91065580f4fa4135969a498e9518c0c8', 'RC son', 'RC_SONC', 'Description: The player of the role is a male child (of any type) of scoping entity (parent)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('127b6fec288746359ed7cd46c88f0193', 0, '91065580f4fa4135969a498e9518c0c8', 'RC stepson', 'RC_STPSON', 'The player of the role is a son of the scoping person''s spouse by a previous union.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('fe61a98b337a459faab6d8950f73c307', 0, '91065580f4fa4135969a498e9518c0c8', 'RC step child', 'RC_STPCHLD', 'The player of the role is a child of the scoping person''s spouse by a previous union.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('0337c32e01b24806a893fe8856b9f1b5', 0, '91065580f4fa4135969a498e9518c0c8', 'RC extended family member', 'RC_EXT', 'Description: A family member not having an immediate genetic or legal relationship e.g. Aunt, cousin, great grandparent, grandchild, grandparent, niece, nephew or uncle.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('89b92e8215da4d678f549ee311f52a2a', 0, '91065580f4fa4135969a498e9518c0c8', 'RC aunt', 'RC_AUNT', 'The player of the role is a sister of the scoping person''s mother or father.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a047f7b38f44404484ad5d69ec14a5e4', 0, '91065580f4fa4135969a498e9518c0c8', 'RC maternal aunt', 'RC_MAUNT', 'Description:The player of the role is a biological sister of the scoping person''s biological mother.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('92b816810d2a45fbb06ebde340130a19', 0, '91065580f4fa4135969a498e9518c0c8', 'RC paternal aunt', 'RC_PAUNT', 'Description:The player of the role is a biological sister of the scoping person''s biological father.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f88411a05dd643ccb992985be76c8908', 0, '91065580f4fa4135969a498e9518c0c8', 'RC cousin', 'RC_COUSN', 'The player of the role is a relative of the scoping person descended from a common ancestor, such as a grandparent, by two or more steps in a diverging line.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('ccba35d9678b49c08dc6aa5908bb373d', 0, '91065580f4fa4135969a498e9518c0c8', 'RC maternal cousin', 'RC_MCOUSN', 'Description:The player of the role is a biological relative of the scoping person descended from a common ancestor on the player''s mother''s side, such as a grandparent, by two or more steps in a diverging line.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('b41b75cce50b480aaefb46ccb036fa5c', 0, '91065580f4fa4135969a498e9518c0c8', 'RC paternal cousin', 'RC_PCOUSN', 'Description:The player of the role is a biological relative of the scoping person descended from a common ancestor on the player''s father''s side, such as a grandparent, by two or more steps in a diverging line.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f2bc75a7c0c14ee0898ee632d9c11764', 0, '91065580f4fa4135969a498e9518c0c8', 'RC great grandparent', 'RC_GGRPRN', 'The player of the role is a parent of the scoping person''s grandparent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('d5604d9916644fd599ad8cd093db8d02', 0, '91065580f4fa4135969a498e9518c0c8', 'RC great grandfather', 'RC_GGRFTH', 'The player of the role is the father of the scoping person''s grandparent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('fbffd3e007a049a5a85846c0d5d3cfec', 0, '91065580f4fa4135969a498e9518c0c8', 'RC maternal greatgrandfather', 'RC_MGGRFTH', 'Description:The player of the role is the biological father of the scoping person''s biological mother''s parent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('d98f538952404648955fdbc1f74affd8', 0, '91065580f4fa4135969a498e9518c0c8', 'RC paternal greatgrandfather', 'RC_PGGRFTH', 'Description:The player of the role is the biological father of the scoping person''s biological father''s parent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('afdc0aa7a2fb48299c6ca248262087f5', 0, '91065580f4fa4135969a498e9518c0c8', 'RC great grandmother', 'RC_GGRMTH', 'The player of the role is the mother of the scoping person''s grandparent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('574b70d75a8049d8bbb88857d4ba7546', 0, '91065580f4fa4135969a498e9518c0c8', 'RC maternal greatgrandmother', 'RC_MGGRMTH', 'Description:The player of the role is the biological mother of the scoping person''s biological mother''s parent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('87635859f1b34d3dba8cff66f05d3a47', 0, '91065580f4fa4135969a498e9518c0c8', 'RC paternal greatgrandmother', 'RC_PGGRMTH', 'Description:The player of the role is the biological mother of the scoping person''s biological father''s parent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('2ded7f929cf44a589cec3dd8a74ee011', 0, '91065580f4fa4135969a498e9518c0c8', 'RC maternal greatgrandparent', 'RC_MGGRPRN', 'Description:The player of the role is a biological parent of the scoping person''s biological mother''s parent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('1cb4038422ff4c9e9ab4a733e867de6b', 0, '91065580f4fa4135969a498e9518c0c8', 'RC paternal greatgrandparent', 'RC_PGGRPRN', 'Description:The player of the role is a biological parent of the scoping person''s biological father''s parent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('970efa0062b244d9b08d725910450f43', 0, '91065580f4fa4135969a498e9518c0c8', 'RC grandchild', 'RC_GRNDCHILD', 'The player of the role is a child of the scoping person''s son or daughter.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('5a47cf787a7449b1b549af72e442e01d', 0, '91065580f4fa4135969a498e9518c0c8', 'RC granddaughter', 'RC_GRNDDAU', 'The player of the role is a daughter of the scoping person''s son or daughter.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('10a8a5e979d54dcb857c3092cedd5591', 0, '91065580f4fa4135969a498e9518c0c8', 'RC grandson', 'RC_GRNDSON', 'The player of the role is a son of the scoping person''s son or daughter.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('6052f4db83544c78be2a15fd3c89a7bd', 0, '91065580f4fa4135969a498e9518c0c8', 'RC grandparent', 'RC_GRPRN', 'The player of the role is a parent of the scoping person''s mother or father.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('21d150c4930e442fb964b34ede4fb348', 0, '91065580f4fa4135969a498e9518c0c8', 'RC grandfather', 'RC_GRFTH', 'The player of the role is the father of the scoping person''s mother or father.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f17753a8298d49b78e37ccfa15060157', 0, '91065580f4fa4135969a498e9518c0c8', 'RC maternal grandfather', 'RC_MGRFTH', 'Description:The player of the role is the biological father of the scoping person''s biological mother.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('3100a18c00a84aad926b6ebeaab4b4a1', 0, '91065580f4fa4135969a498e9518c0c8', 'RC paternal grandfather', 'RC_PGRFTH', 'Description:The player of the role is the biological father of the scoping person''s biological father.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('9d95989bd0714eb39ac6d3f691bafee1', 0, '91065580f4fa4135969a498e9518c0c8', 'RC grandmother', 'RC_GRMTH', 'The player of the role is the mother of the scoping person''s mother or father.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('ec36bc5cacf54ef1b6591ac78ee968da', 0, '91065580f4fa4135969a498e9518c0c8', 'RC maternal grandmother', 'RC_MGRMTH', 'Description:The player of the role is the biological mother of the scoping person''s biological mother.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('0bbece14c1884d5b91b08999291fefc7', 0, '91065580f4fa4135969a498e9518c0c8', 'RC paternal grandmother', 'RC_PGRMTH', 'Description:The player of the role is the biological mother of the scoping person''s biological father.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e8e3924fc2e845a694003727b37d7f98', 0, '91065580f4fa4135969a498e9518c0c8', 'RC maternal grandparent', 'RC_MGRPRN', 'Description:The player of the role is the biological parent of the scoping person''s biological mother.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('3e3184f06e9047c2890bb8a284e000d5', 0, '91065580f4fa4135969a498e9518c0c8', 'RC paternal grandparent', 'RC_PGRPRN', 'Description:The player of the role is the biological parent of the scoping person''s biological father.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('5ea797ffca674ddbbaf208964ca26e34', 0, '91065580f4fa4135969a498e9518c0c8', 'RC inlaw', 'RC_INLAW', 'A relationship between an individual and a member of their spousal partner''s immediate family.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('1e6f1da915e84512b5026699b7ea7c34', 0, '91065580f4fa4135969a498e9518c0c8', 'RC childinlaw', 'RC_CHLDINLAW', 'The player of the role is the spouse of scoping person''s child.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('fcff1dc5b8604e8dbee1cfac40b33944', 0, '91065580f4fa4135969a498e9518c0c8', 'RC daughter inlaw', 'RC_DAUINLAW', 'The player of the role is the wife of scoping person''s son.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a8c0f886aae3479e8aa3d8e37f31b9be', 0, '91065580f4fa4135969a498e9518c0c8', 'RC son inlaw', 'RC_SONINLAW', 'The player of the role is the husband of scoping person''s daughter.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('41d6001a9fc94337bbb6ce1ff284e115', 0, '91065580f4fa4135969a498e9518c0c8', 'RC parent inlaw', 'RC_PRNINLAW', 'The player of the role is the parent of scoping person''s husband or wife.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('140d489763554be8994706e0ca12b1ed', 0, '91065580f4fa4135969a498e9518c0c8', 'RC fatherinlaw', 'RC_FTHINLAW', 'The player of the role is the father of the scoping person''s husband or wife.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('d5c679c8292c4e7884a3fcccbf7b4251', 0, '91065580f4fa4135969a498e9518c0c8', 'RC motherinlaw', 'RC_MTHINLAW', 'The player of the role is the mother of the scoping person''s husband or wife.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8f8c7f019cfa477ab6eefe595f8284cd', 0, '91065580f4fa4135969a498e9518c0c8', 'RC sibling inlaw', 'RC_SIBINLAW', 'The player of the role is: (1) a sibling of the scoping person''s spouse, or (2) the spouse of the scoping person''s sibling, or (3) the spouse of a sibling of the scoping person''s spouse.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('19d9601ffc9743029b338abd194b2b06', 0, '91065580f4fa4135969a498e9518c0c8', 'RC brotherinlaw', 'RC_BROINLAW', 'The player of the role is: (1) a brother of the scoping person''s spouse, or (2) the husband of the scoping person''s sister, or (3) the husband of a sister of the scoping person''s spouse.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('7612c8873ae64056800d3c9af1242e72', 0, '91065580f4fa4135969a498e9518c0c8', 'RC sisterinlaw', 'RC_SISINLAW', 'The player of the role is: (1) a sister of the scoping person''s spouse, or (2) the wife of the scoping person''s brother, or (3) the wife of a brother of the scoping person''s spouse.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('9162f3544fdc45ed808e41e9335ea325', 0, '91065580f4fa4135969a498e9518c0c8', 'RC niece/nephew', 'RC_NIENEPH', 'The player of the role is a child of scoping person''s brother or sister or of the brother or sister of the scoping person''s spouse.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('9324244d819941b19fb13bf3566d5d57', 0, '91065580f4fa4135969a498e9518c0c8', 'RC nephew', 'RC_NEPHEW', 'The player of the role is a son of the scoping person''s brother or sister or of the brother or sister of the scoping person''s spouse.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('02918b9251b64da091b0b52ddbb5e94f', 0, '91065580f4fa4135969a498e9518c0c8', 'RC niece', 'RC_NIECE', 'The player of the role is a daughter of the scoping person''s brother or sister or of the brother or sister of the scoping person''s spouse.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('dadccbc8dfb245728f19d07e325fcc7c', 0, '91065580f4fa4135969a498e9518c0c8', 'RC uncle', 'RC_UNCLE', 'The player of the role is a brother of the scoping person''s mother or father.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('d3a4de1f3d6d4ab3bff5795e7c20a075', 0, '91065580f4fa4135969a498e9518c0c8', 'RC maternal uncle', 'RC_MUNCLE', 'Description:The player of the role is a biological brother of the scoping person''s biological mother.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('2d0372991e3d42768d131ee73cb43479', 0, '91065580f4fa4135969a498e9518c0c8', 'RC paternal uncle', 'RC_PUNCLE', 'Description:The player of the role is a biological brother of the scoping person''s biological father.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('758fc29886b14ec0a46391034a0e35c9', 0, '91065580f4fa4135969a498e9518c0c8', 'RC parent', 'RC_PRN', 'The player of the role is one who begets, gives birth to, or nurtures and raises the scoping entity (child).');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('77bf1643335240b3ae63450b2baa37f6', 0, '91065580f4fa4135969a498e9518c0c8', 'RC adoptive parent', 'RC_ADOPTP', 'The player of the role (parent) has taken the scoper (child) into their family through legal means and raises them as his or her own child.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a7925456430d4c2db3b27afbc3bbde0f', 0, '91065580f4fa4135969a498e9518c0c8', 'RC adoptive father', 'RC_ADOPTF', 'The player of the role (father) is a male who has taken the scoper (child) into their family through legal means and raises them as his own child.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('ddd1f6d3e36244cd98875f7d3daaa7dd', 0, '91065580f4fa4135969a498e9518c0c8', 'RC adoptive mother', 'RC_ADOPTM', 'The player of the role (father) is a female who has taken the scoper (child) into their family through legal means and raises them as her own child.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('5a395a3de1be4b88a91d99b1965cfb3b', 0, '91065580f4fa4135969a498e9518c0c8', 'RC father', 'RC_FTH', 'The player of the role is a male who begets or raises or nurtures the scoping entity (child).');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('7ba01ba354e040348ffdccee1454f9f1', 0, '91065580f4fa4135969a498e9518c0c8', 'RC foster father', 'RC_FTHFOST', 'The player of the role (parent) who is a male statecertified caregiver responsible for the scoper (child) who has been placed in the parent''s care. The placement of the child is usually arranged through the government or a socialservice agency, and temporary. The state, via a jurisdiction recognized child protection agency, stands as in loco parentis to the child, making all legal decisions while the foster parent is responsible for the daytoday care of the specified child.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('43cd042172e9449ca70afd50ffaea8c9', 0, '91065580f4fa4135969a498e9518c0c8', 'RC natural father', 'RC_NFTH', 'The player of the role is a male who begets the scoping entity (child).');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('cf62ae76136d41439a1782ec94d4e0bf', 0, '91065580f4fa4135969a498e9518c0c8', 'RC natural father of fetus', 'RC_NFTHF', 'Indicates the biologic male parent of a fetus.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('edb4819506444e15b4a76edfe3c8062c', 0, '91065580f4fa4135969a498e9518c0c8', 'RC stepfather', 'RC_STPFTH', 'The player of the role is the husband of scoping person''s mother and not the scoping person''s natural father.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('08f80790dda34ec1939c5884ac1a82d3', 0, '91065580f4fa4135969a498e9518c0c8', 'RC mother', 'RC_MTH', 'The player of the role is a female who conceives, gives birth to, or raises and nurtures the scoping entity (child).');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('fcc2d4cec44145429f70ba72f0a3a6df', 0, '91065580f4fa4135969a498e9518c0c8', 'RC gestational mother', 'RC_GESTM', 'The player is a female whose womb carries the fetus of the scoper. Generally used when the gestational mother and natural mother are not the same.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('0814206732b24b19a01c63f336e0726b', 0, '91065580f4fa4135969a498e9518c0c8', 'RC foster mother', 'RC_MTHFOST', 'The player of the role (parent) who is a female statecertified caregiver responsible for the scoper (child) who has been placed in the parent''s care. The placement of the child is usually arranged through the government or a socialservice agency, and temporary. The state, via a jurisdiction recognized child protection agency, stands as in loco parentis to the child, making all legal decisions while the foster parent is responsible for the daytoday care of the specified child.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8df34897304846c58cadbd9b622b9bbb', 0, '91065580f4fa4135969a498e9518c0c8', 'RC natural mother', 'RC_NMTH', 'The player of the role is a female who conceives or gives birth to the scoping entity (child).');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('95f371738a3541489cdaa9781bd19f86', 0, '91065580f4fa4135969a498e9518c0c8', 'RC natural mother of fetus', 'RC_NMTHF', 'The player is the biologic female parent of the scoping fetus.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('cd3a2e6ff8044c89839ba42917122ec9', 0, '91065580f4fa4135969a498e9518c0c8', 'RC stepmother', 'RC_STPMTH', 'The player of the role is the wife of scoping person''s father and not the scoping person''s natural mother.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('81965cc5984e4c9691022ad03ffbcbef', 0, '91065580f4fa4135969a498e9518c0c8', 'RC natural parent', 'RC_NPRN', 'natural parent');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e736e6edf23748ec91ca60840ea56d60', 0, '91065580f4fa4135969a498e9518c0c8', 'RC foster parent', 'RC_PRNFOST', 'The player of the role (parent) who is a statecertified caregiver responsible for the scoper (child) who has been placed in the parent''s care. The placement of the child is usually arranged through the government or a socialservice agency, and temporary. The state, via a jurisdiction recognized child protection agency, stands as in loco parentis to the child, making all legal decisions while the foster parent is responsible for the daytoday care of the specified child.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8457ff348a3c4e8381b62991b2ca4aca', 0, '91065580f4fa4135969a498e9518c0c8', 'RC step parent', 'RC_STPPRN', 'The player of the role is the spouse of the scoping person''s parent and not the scoping person''s natural parent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('315774e5a8814d09b21fb2640c9ac0e8', 0, '91065580f4fa4135969a498e9518c0c8', 'RC sibling', 'RC_SIB', 'The player of the role shares one or both parents in common with the scoping entity.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('98082eca40c54d159ed48ecd844e3ae7', 0, '91065580f4fa4135969a498e9518c0c8', 'RC brother', 'RC_BRO', 'The player of the role is a male sharing one or both parents in common with the scoping entity.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('48bcbb2b2b954266a756f277c81857eb', 0, '91065580f4fa4135969a498e9518c0c8', 'RC halfbrother', 'RC_HBRO', 'The player of the role is a male related to the scoping entity by sharing only one biological parent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f2be630f1d574ae0a094b4d4257c33f9', 0, '91065580f4fa4135969a498e9518c0c8', 'RC natural brother', 'RC_NBRO', 'The player of the role is a male having the same biological parents as the scoping entity.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('5181d4e68c984b439dfbfd79a2daac28', 0, '91065580f4fa4135969a498e9518c0c8', 'RC twin brother', 'RC_TWINBRO', 'The scoper was carried in the same womb as the male player and shares common biological parents.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f24c3a34b07a4397ae2d44cef62e1a0a', 0, '91065580f4fa4135969a498e9518c0c8', 'RC fraternal twin brother', 'RC_FTWINBRO', 'The scoper was carried in the same womb as the male player and shares common biological parents but is the product of a distinct egg/sperm pair.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('39ab35761b7f423e89a175e52f7c9364', 0, '91065580f4fa4135969a498e9518c0c8', 'RC identical twin brother', 'RC_ITWINBRO', 'The male scoper is an offspring of the same eggsperm pair as the male player.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('0a56be2e819c4ff187bf0cafb9e217d0', 0, '91065580f4fa4135969a498e9518c0c8', 'RC stepbrother', 'RC_STPBRO', 'The player of the role is a son of the scoping person''s stepparent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('5d52e4bd234743a18c61107a371163f4', 0, '91065580f4fa4135969a498e9518c0c8', 'RC halfsibling', 'RC_HSIB', 'The player of the role is related to the scoping entity by sharing only one biological parent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('513ec433fb864c22a8d1c9b8244e69c2', 0, '91065580f4fa4135969a498e9518c0c8', 'RC halfsister', 'RC_HSIS', 'The player of the role is a female related to the scoping entity by sharing only one biological parent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f84fece85ee343c490854ff3c43235ee', 0, '91065580f4fa4135969a498e9518c0c8', 'RC natural sibling', 'RC_NSIB', 'The player of the role has both biological parents in common with the scoping entity.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('0c409220413e4125bf308b4f7d10ef0a', 0, '91065580f4fa4135969a498e9518c0c8', 'RC natural sister', 'RC_NSIS', 'The player of the role is a female having the same biological parents as the scoping entity.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('138fea243a1a4e8ea91bd4b61fbd2917', 0, '91065580f4fa4135969a498e9518c0c8', 'RC twin sister', 'RC_TWINSIS', 'The scoper was carried in the same womb as the female player and shares common biological parents.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('2f9e14ddd88e4522ad056230a49b05fb', 0, '91065580f4fa4135969a498e9518c0c8', 'RC fraternal twin sister', 'RC_FTWINSIS', 'The scoper was carried in the same womb as the female player and shares common biological parents but is the product of a distinct egg/sperm pair.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a234790420e448adb957f14bf7c95cd4', 0, '91065580f4fa4135969a498e9518c0c8', 'RC identical twin sister', 'RC_ITWINSIS', 'The female scoper is an offspring of the same eggsperm pair as the female player.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4754b7178d214d07957bbe834f4b0e8d', 0, '91065580f4fa4135969a498e9518c0c8', 'RC twin', 'RC_TWIN', 'The scoper and player were carried in the same womb and shared common biological parents.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('768a7c4c8cb9485182f8a104778eff0c', 0, '91065580f4fa4135969a498e9518c0c8', 'RC fraternal twin', 'RC_FTWIN', 'The scoper and player were carried in the same womb and share common biological parents but are the product of distinct egg/sperm pairs.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('18c47e4d1bf1404da99a356fe0a26610', 0, '91065580f4fa4135969a498e9518c0c8', 'RC identical twin', 'RC_ITWIN', 'The scoper and player are offspring of the same eggsperm pair.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('913bf93704fe4b67a00f6aeafe63c78a', 0, '91065580f4fa4135969a498e9518c0c8', 'RC sister', 'RC_SIS', 'The player of the role is a female sharing one or both parents in common with the scoping entity.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('11e11119083f4e7f8776713468113260', 0, '91065580f4fa4135969a498e9518c0c8', 'RC stepsister', 'RC_STPSIS', 'The player of the role is a daughter of the scoping person''s stepparent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('b6ca53f429b442f4baa8e952b697d6f2', 0, '91065580f4fa4135969a498e9518c0c8', 'RC step sibling', 'RC_STPSIB', 'The player of the role is a child of the scoping person''s stepparent.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('6702e52be5694c848f2f9777c0f2276b', 0, '91065580f4fa4135969a498e9518c0c8', 'RC significant other', 'RC_SIGOTHR', 'A person who is important to one''s well being; especially a spouse or one in a similar relationship. (The player is the one who is important)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('79531da0f1e6499ea5298868db390b97', 0, '91065580f4fa4135969a498e9518c0c8', 'RC domestic partner', 'RC_DOMPART', 'The player of the role cohabits with the scoping person but is not the scoping person''s spouse.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('dc5a3effd84243a6ae2c63df221bb60f', 0, '91065580f4fa4135969a498e9518c0c8', 'RC former spouse', 'RC_FMRSPS', 'Player of the role was previously joined to the scoping person in marriage and this marriage is now dissolved and inactive. Usage Note: This is significant to indicate as some jurisdictions have different legal requirements for former spouse to access the patient''s record, from a general friend.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('bb32e6c7667f4cf39347aa89781edc64', 0, '91065580f4fa4135969a498e9518c0c8', 'RC spouse', 'RC_SPS', 'The player of the role is a marriage partner of the scoping person.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('bc9352a363c04ba2ac5120c0cbca0ea8', 0, '91065580f4fa4135969a498e9518c0c8', 'RC husband', 'RC_HUSB', 'The player of the role is a man joined to a woman (scoping person) in marriage.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('b20f2abfe6a54030928f24a71f3629bf', 0, '91065580f4fa4135969a498e9518c0c8', 'RC wife', 'RC_WIFE', 'The player of the role is a woman joined to a man (scoping person) in marriage.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a543dd562e52418ea1229372a2c26110', 0, '91065580f4fa4135969a498e9518c0c8', 'RC unrelated friend', 'RC_FRND', 'The player of the role is a person who is known, liked, and trusted by the scoping person.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('2f2d6977d35a451a9aa8f4aee841cbae', 0, '91065580f4fa4135969a498e9518c0c8', 'RC neighbor', 'RC_NBOR', 'The player of the role lives near or next to the scoping person.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('ad121009f80644a8921ccba728a81940', 0, '91065580f4fa4135969a498e9518c0c8', 'RC self', 'RC_ONESELF', 'The relationship that a person has with his or her self.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('935baf1211f442ba89b570d6acea6546', 0, '91065580f4fa4135969a498e9518c0c8', 'RC Roommate', 'RC_ROOM', 'One who shares living quarters with the subject.');

INSERT INTO fhir_system_code(id, version, system_id, system_code, code_id, system_code_value, display_name)
VALUES ('4b1d6fa36c6e46439f6dde14e1c36fff', 0, '3ffa1fe58ded4f18afa6d847d2b94217', 'CP', 'aa36eeb633f14637936ac934abdaf83f', 'http://hl7.org/fhir/v2/0131|CP', 'CP');
INSERT INTO fhir_system_code(id, version, system_id, system_code, code_id, system_code_value, display_name)
VALUES ('cdc6b6697c42460bad34aa65f8655145', 0, '3ffa1fe58ded4f18afa6d847d2b94217', 'C', '774ee335a7034753b524da49e4f10454', 'http://hl7.org/fhir/v2/0131|C', 'C');
INSERT INTO fhir_system_code(id, version, system_id, system_code, code_id, system_code_value, display_name)
VALUES ('14116083429a437c9023c0f4095679a6', 0, '64d3e785d6a54be59bc95ed9b938e2f6', 'PRN', '758fc29886b14ec0a46391034a0e35c9', 'http://hl7.org/fhir/v3/RoleCode|PRN', 'PRN');
INSERT INTO fhir_system_code(id, version, system_id, system_code, code_id, system_code_value, display_name)
VALUES ('41b1d0ae68b0433fa2a1f57bbce57802', 0, '64d3e785d6a54be59bc95ed9b938e2f6', 'ADOPTF', 'a7925456430d4c2db3b27afbc3bbde0f', 'http://hl7.org/fhir/v3/RoleCode|ADOPTF', 'ADOPTF');
INSERT INTO fhir_system_code(id, version, system_id, system_code, code_id, system_code_value, display_name)
VALUES ('4491d5e330d540d3b63a4c4d6ada0045', 0, '64d3e785d6a54be59bc95ed9b938e2f6', 'ADOPTM', 'ddd1f6d3e36244cd98875f7d3daaa7dd', 'http://hl7.org/fhir/v3/RoleCode|ADOPTM', 'ADOPTM');
INSERT INTO fhir_system_code(id, version, system_id, system_code, code_id, system_code_value, display_name)
VALUES ('7e5d84308ccb4a6d8e16a11a0c0d2f75', 0, '64d3e785d6a54be59bc95ed9b938e2f6', 'FTH', '5a395a3de1be4b88a91d99b1965cfb3b', 'http://hl7.org/fhir/v3/RoleCode|FTH', 'FTH');
INSERT INTO fhir_system_code(id, version, system_id, system_code, code_id, system_code_value, display_name)
VALUES ('0b2ca2b94edb4da69e34b783575eea0b', 0, '64d3e785d6a54be59bc95ed9b938e2f6', 'MTH', '08f80790dda34ec1939c5884ac1a82d3', 'http://hl7.org/fhir/v3/RoleCode|MTH', 'MTH');

-- Script that extracts Organisation Unit Reference from Patient
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('a250e109a13542b28bdb1c050c1d384c', 0, 'Org Unit Code from FHIR Resource', 'EXTRACT_FHIR_RESOURCE_DHIS_ORG_UNIT_CODE',
'Extracts the organization unit code reference from the input FHIR resource.',
'EVALUATE', 'ORG_UNIT_REF', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a250e109a13542b28bdb1c050c1d384c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a250e109a13542b28bdb1c050c1d384c', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('7b94febabcf64635929a01311b25d975', 0, 'a250e109a13542b28bdb1c050c1d384c',
'var ref = null;
var organizationReference = null;
if (input.managingOrganization)
{
  organizationReference = input.managingOrganization;
}
if (((organizationReference == null) || organizationReference.isEmpty()) && args[''useLocations''] && input.location && !input.getLocation().isEmpty())
{
  var location = referenceUtils.getResource(input.location, ''LOCATION'');
  if ((location != null) && location.managingOrganization && !location.getManagingOrganization.isEmpty())
  {
    organizationReference = location.managingOrganization;
  }
}
if (organizationReference != null)
{
  var mappedCode = null;
  var hierarchy = organizationUtils.findHierarchy(organizationReference);
  if (hierarchy != null)
  {
    for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
	  {
      var code = identifierUtils.getResourceIdentifier(hierarchy.get(i), ''ORGANIZATION'');
      if (code != null)
      {
        mappedCode = codeUtils.getMappedCode(code, ''ORGANIZATION'');
        if ((mappedCode == null) && args[''useIdentifierCode''])
        {
          mappedCode = organizationUtils.existsWithPrefix(code);
        }
      }
	  }
  }
  if (mappedCode == null)
  {
    mappedCode = args[''defaultCode''];
  }
  if (mappedCode != null)
  {
    ref = context.createReference(mappedCode, ''CODE'');
  }
}
if ((ref == null) && args[''useTei''] && (typeof trackedEntityInstance !== ''undefined''))
{
  ref = context.createReference(trackedEntityInstance.organizationUnitId, ''ID'');
}
ref', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('7b94febabcf64635929a01311b25d975', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('7b94febabcf64635929a01311b25d975', 'R4');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('c017573383fc4de29cd0a2ae6b92e991', 0, 'a250e109a13542b28bdb1c050c1d384c',
'useLocations', 'BOOLEAN', TRUE, 'true',
'Specifies if alternatively the managing organization of an included location should be used when the input itself does not contain a managing organization.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('33e66e7a32cc4a2e8224519e790c8ad2', 0, 'a250e109a13542b28bdb1c050c1d384c',
'useIdentifierCode', 'BOOLEAN', TRUE, 'true',
'Specifies if the identifier code itself with the default code prefix for organizations should be used as fallback when no code mapping for the identifier code exists.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('2db146ac189548e09d24e81c7f8a7033', 0, 'a250e109a13542b28bdb1c050c1d384c',
'defaultCode', 'CODE', FALSE, null,
'Specifies the default DHIS2 organization unit code that should be used when no other matching DHIS2 organization unit cannot be found.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ef387255d1df48a3955d2300bedf1f99', 0, 'a250e109a13542b28bdb1c050c1d384c',
'useTei', 'BOOLEAN', TRUE, 'true',
'Specifies if the organization unit of the tracked entity instance (if any) should be used as last fallback when no other organization unit can be found.');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('25a97bb47b394ed48677db4bcaa28ccf', 0, 'a250e109a13542b28bdb1c050c1d384c', 'Org Unit Code from FHIR Resource', 'EXTRACT_FHIR_RESOURCE_DHIS_ORG_UNIT_CODE',
'Extracts the organization unit code reference from the input FHIR resource.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('9de91cc0979b43b099d5fc3ee76fd74d', '25a97bb47b394ed48677db4bcaa28ccf', '33e66e7a32cc4a2e8224519e790c8ad2', 'true');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('b19e4cc20ded4410b5d85e971e48fd93', '25a97bb47b394ed48677db4bcaa28ccf', '2db146ac189548e09d24e81c7f8a7033', NULL);

-- Script that transforms a Related Person to contact details of a Person
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('05fb37c27e6845c0bfe786999492e202', 0, 'Transforms FHIR Related Person to DHIS Person', 'TRANSFORM_FHIR_RELATED_PERSON_DHIS_PERSON',
'Transforms FHIR Related Person to DHIS Person.', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_RELATED_PERSON',
'DHIS_TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('05fb37c27e6845c0bfe786999492e202', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('05fb37c27e6845c0bfe786999492e202', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('05fb37c27e6845c0bfe786999492e202', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('4476320e3efa40cca2b267c9b1255ce6', 0, '05fb37c27e6845c0bfe786999492e202',
'personFirstNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, 'ID:ftFBu8mHZ4H',
'The reference of the tracked entity attribute that contains the first name of the related Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('46936186a2df4f3d9d8f8adb7b3028cd', 0, '05fb37c27e6845c0bfe786999492e202',
'personLastNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, 'ID:EpbquVl5OD6',
'The reference of the tracked entity attribute that contains the last name of the related Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('a8a6a2d51bef4497a0f485727a79be0e', 0, '05fb37c27e6845c0bfe786999492e202',
'personPhoneAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, 'ID:pjexi5YaAPa',
'The reference of the tracked entity attribute that contains the phone number of the related Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, array_value, mandatory, default_value, description)
VALUES ('198df286b613479a8b4d3cb9961105e1', 0, '05fb37c27e6845c0bfe786999492e202',
'relationshipTypeCodes', 'CODE', TRUE, TRUE, 'RC_C|RC_CP|RC_PRN|RC_ADOPTM|RC_ADOPTF|RC_MTH|RC_FTH',
'Codes of patient relationship types in their preferred order.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('bee0e77cd2a54950ae2e2e399c1c3629', 0, '05fb37c27e6845c0bfe786999492e202',
'preferredGender', 'GENDER', FALSE, 'FEMALE',
'The preferred gender of the person (e.g. female in case of a mother).');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('0cb05413c78a4b2797fca73c3cb9c430', 0, '05fb37c27e6845c0bfe786999492e202',
'function getMatchingRelatedPerson(relatedPersons, relationshipTypeCodes, preferredGender)
{
  var groupedSystemCodeValues = codeUtils.getSystemCodeValuesByMappingCodes(relationshipTypeCodes);
  for (var j = 0; j < relationshipTypeCodes.length; j++ )
  {
    var systemCodeValues = groupedSystemCodeValues[relationshipTypeCodes[j]];
    if (systemCodeValues != null)
    {
      var matchingRelatedPerson = null;
      for (var k = 0; k < relatedPersons.length; k++)
      {
        if (codeUtils.containsAnyCode(relatedPersons[k].getRelationship(), systemCodeValues))
        {
          if (preferredGender == null)
          {
            return relatedPersons[k];
          }
          if ((relatedPersons[k].getGender() != null) && (relatedPersons[k].getGender().name() === preferredGender))
          {
            return relatedPersons[k];
          }
          if (matchingRelatedPerson == null)
          {
            matchingRelatedPerson = relatedPersons[k];
          }
        }
      }
      if (matchingRelatedPerson != null)
      {
        return matchingRelatedPerson;
      }
    }
  }
  return null;
}
var patient = referenceUtils.getResource(input.patient, ''PATIENT'', true);
if ((patient != null) && patient.hasLink())
{
  var relatedPersons = [];
  for (var i = 0; i < patient.getLink().size(); i++)
  {
    var link = patient.getLink().get(i);
    if (!link.isEmpty() && !link.getOther().isEmpty())
    {
      var relatedPerson = referenceUtils.getResource(link.getOther(), ''RELATED_PERSON'');
      if ((relatedPerson != null) && (!relatedPerson.hasActive() || relatedPerson.getActive()) && dateTimeUtils.isValidNow(relatedPerson.getPeriod()))
      {
        relatedPersons.push(relatedPerson);
      }
    }
  }
  if (relatedPersons.length > 0)
  {
    var matchingRelatedPerson = getMatchingRelatedPerson(relatedPersons, args[''relationshipTypeCodes''], args[''preferredGender'']);
    if (matchingRelatedPerson != null)
    {
      var lastName = humanNameUtils.getPrimaryName(matchingRelatedPerson.name).family;
      if ((lastName != null) || args[''resetDhisValue''])
      {
        output.setOptionalValue( args[''personLastNameAttribute''], lastName, context.getFhirRequest().getLastUpdated() );
      }
      var firstName = humanNameUtils.getSingleGiven(humanNameUtils.getPrimaryName(matchingRelatedPerson.name));
      if ((firstName != null) || args[''resetDhisValue''])
      {
        output.setOptionalValue( args[''personFirstNameAttribute''], firstName, context.getFhirRequest().getLastUpdated() );
      }
      var phone = contactPointUtils.getPhoneContactPointValue(matchingRelatedPerson.telecom);
      if ((phone != null) || args[''resetDhisValue''])
      {
        output.setOptionalValue( args[''personPhoneAttribute''], phone, context.getFhirRequest().getLastUpdated() );
      }
    }
  }
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('0cb05413c78a4b2797fca73c3cb9c430', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('0cb05413c78a4b2797fca73c3cb9c430', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('c6b2d08d3a73434ea5afee0ff13549a1', 0, '05fb37c27e6845c0bfe786999492e202',
'Transforms FHIR Related Person to DHIS Person', 'TRANSFORM_FHIR_RELATED_PERSON_DHIS_PERSON',
'Transforms FHIR Related Person to DHIS Person.');

-- Script that extracts GEO location from an Address
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('2263b2969d964698bc1d17930005eef3', 0, 'GEO Location from Patient', 'EXTRACT_ADDRESS_GEO_LOCATION',
'Extracts the GEO location form an address that is included in the input.',
'EVALUATE', 'LOCATION', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('2263b2969d964698bc1d17930005eef3', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('2263b2969d964698bc1d17930005eef3', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('039ac2e650f24e4a9e4adc0515560273', 0, '2263b2969d964698bc1d17930005eef3',
'var location = null;
if (input.address)
{
  location = geoUtils.getLocation(addressUtils.getPrimaryAddress(input.address));
}
location', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('039ac2e650f24e4a9e4adc0515560273', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('039ac2e650f24e4a9e4adc0515560273', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('ef90531f443848bd83b36370dd65875a', 0, '2263b2969d964698bc1d17930005eef3',  'GEO Location from Address', 'EXTRACT_ADDRESS_GEO_LOCATION',
'Extracts the GEO location form an address that is included in the input.');

-- Script that performs the lookup of TEI FHIR Resource (Patient) from FHIR Related Person
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('0cf409009de3468cbfd0fa2a4703fb66', 0, 'Related Person TEI Lookup', 'RELATED_PERSON_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Related Person.', 'EVALUATE', 'FHIR_RESOURCE', 'FHIR_RELATED_PERSON', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('0cf409009de3468cbfd0fa2a4703fb66', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('0cf409009de3468cbfd0fa2a4703fb66', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('1f94dda828ec480f8c6bd8d734612414', 0, '0cf409009de3468cbfd0fa2a4703fb66', 'referenceUtils.getResource(input.patient, ''PATIENT'')', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('1f94dda828ec480f8c6bd8d734612414', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('1f94dda828ec480f8c6bd8d734612414', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('26e8880864ee446980837962b74ac48a', 0, '0cf409009de3468cbfd0fa2a4703fb66',
'Related Person TEI Lookup', 'RELATED_PERSON_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Related Person.');

-- Script that transforms Patient to Person
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('ea8879435e944e319441c7661fe1063e', 0, 'Transforms FHIR Patient to DHIS Person', 'TRANSFORM_FHIR_PATIENT_DHIS_PERSON', 'Transforms FHIR Patient to DHIS Person.', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_PATIENT', 'DHIS_TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea8879435e944e319441c7661fe1063e', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea8879435e944e319441c7661fe1063e', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea8879435e944e319441c7661fe1063e', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('276b26f2ba0141e689c6b1100580b1f3', 0, 'ea8879435e944e319441c7661fe1063e',
'uniqueIdAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, 'ID:KSr2yTdu1AI',
'The reference of the tracked entity attribute that contains a unique ID and should be set to the identifier that is used by FHIR.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('0a7c26cb7bd343949d47a610ac231f8a', 0, 'ea8879435e944e319441c7661fe1063e',
'lastNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, 'ID:aW66s2QSosT',
'The reference of the tracked entity attribute that contains the last name of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('b41dd571a1294fa6a80735ea5663e8e3', 0, 'ea8879435e944e319441c7661fe1063e',
'firstNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, 'ID:TfdH5KvFmMy',
'The reference of the tracked entity attribute that contains the first name of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('9c62145d55a64e3fbfb8df81ae43146a', 0, 'ea8879435e944e319441c7661fe1063e',
'resetDhisValue', 'BOOLEAN', TRUE, 'false', 'Specifies if existing values in DHIS can be reset by null values (except first and last name).');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('90b3c11038e44291934ce2569e8af1ba', 0, 'ea8879435e944e319441c7661fe1063e',
'birthDateAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, 'ID:BiTsLcJQ95V',
'The reference of the tracked entity attribute that contains the birth date of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('8e3efdc76ce44899bb20faed7d5e3279', 0, 'ea8879435e944e319441c7661fe1063e',
'genderAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, 'ID:CklPZdOd6H1',
'The reference of the tracked entity attribute that contains the gender of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ae13ceca86d74f608d5425587d53a5bd', 0, 'ea8879435e944e319441c7661fe1063e',
'addressTextAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, 'ID:Y0i71Y6CVdy',
'The reference of the tracked entity attribute that contains as most as possible from the address of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('4a156bee59354bcc80b8e944a08a2988', 0, 'ea8879435e944e319441c7661fe1063e',
'middleNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the middle name of the Person.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('b2cfaf306ede41f2bd6c448e76c429a1', 0, 'ea8879435e944e319441c7661fe1063e',
'output.setOptionalValue(args[''uniqueIdAttribute''], output.getIdentifier());
output.setValue(args[''lastNameAttribute''], humanNameUtils.getPrimaryName(input.name).family, context.getFhirRequest().getLastUpdated());
output.setValue(args[''firstNameAttribute''], humanNameUtils.getSingleGiven(humanNameUtils.getPrimaryName(input.name)), context.getFhirRequest().getLastUpdated());
var birthDate = dateTimeUtils.getPreciseDate(input.birthDateElement);
if ((birthDate != null) || args[''resetDhisValue''])
{
  output.setOptionalValue(args[''birthDateAttribute''], birthDate, context.getFhirRequest().getLastUpdated());
}
if ((input.gender != null) || args[''resetDhisValue''])
{
  output.setOptionalValue(args[''genderAttribute''], input.gender, context.getFhirRequest().getLastUpdated());
}
var addressText = addressUtils.getConstructedText(addressUtils.getPrimaryAddress(input.address));
if ((addressText != null) || args[''resetDhisValue''])
{
  output.setOptionalValue(args[''addressTextAttribute''], addressText, context.getFhirRequest().getLastUpdated());
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b2cfaf306ede41f2bd6c448e76c429a1', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b2cfaf306ede41f2bd6c448e76c429a1', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('72451c8f7492470790b8a3e0796de19e', 0, 'ea8879435e944e319441c7661fe1063e',
'Transforms FHIR Patient to DHIS Person', 'TRANSFORM_FHIR_PATIENT_DHIS_PERSON', 'Transforms FHIR Patient to DHIS Person.');

-- Tracked Entity Person
INSERT INTO fhir_tracked_entity(id, version, name, description, tracked_entity_ref, tracked_entity_identifier_ref)
VALUES ('4203754d21774a4486aa2de31ee4c8ee', 0, 'Person', 'Tracked entity for a patient.', 'NAME:Person', 'ID:Ewi7FUfcHAD');

-- Rule FHIR Patient to tracked entity type Person
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, transform_imp_script_id, simple_fhir_id)
VALUES ('5f9ebdc9852e4c8387ca795946aabc35', 0, 'FHIR Patient to Person', NULL, TRUE, 0, 'PATIENT', 'TRACKED_ENTITY', '72451c8f7492470790b8a3e0796de19e', TRUE);
INSERT INTO fhir_tracked_entity_rule (id, tracked_entity_id, org_lookup_script_id, loc_lookup_script_id)
VALUES ('5f9ebdc9852e4c8387ca795946aabc35', '4203754d21774a4486aa2de31ee4c8ee', '25a97bb47b394ed48677db4bcaa28ccf', 'ef90531f443848bd83b36370dd65875a');

-- Rule FHIR Related Person to tracked entity type Person
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, transform_imp_script_id, contained_allowed, simple_fhir_id)
VALUES ('52227dd9c79c478b92af9aa1f33c76fd', 0, 'FHIR Related Person to Person', NULL, TRUE, 0, 'RELATED_PERSON', 'TRACKED_ENTITY', 'c6b2d08d3a73434ea5afee0ff13549a1', TRUE, TRUE);
INSERT INTO fhir_tracked_entity_rule (id, tracked_entity_id, tei_lookup_script_id)
VALUES ('52227dd9c79c478b92af9aa1f33c76fd', '4203754d21774a4486aa2de31ee4c8ee', '26e8880864ee446980837962b74ac48a');

INSERT INTO fhir_dhis_sync_group(id, version)
VALUES ('22204dd405d94cdd96a8ed742087d469', 0);
INSERT INTO fhir_dhis_sync_group_update(id)
VALUES ('22204dd405d94cdd96a8ed742087d469');

INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('c8ca1fbd4d954911b16ea2310af3533f', 0, 'DHIS2 FHIR Adapter Patient Identifier', 'SYSTEM_DHIS2_FHIR_PATIENT_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier',
        'DHIS2 FHIR Adapter Patient Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('cc46b8acaced4bd2b10403a7d34da438', 0, 'DHIS2 FHIR Adapter Location Identifier', 'SYSTEM_DHIS2_FHIR_LOCATION_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/location-identifier',
        'DHIS2 FHIR Adapter Location Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('db955a8aca584263bc56faa99085df93', 0, 'DHIS2 FHIR Adapter Organization Identifier', 'SYSTEM_DHIS2_FHIR_ORGANIZATION_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/organization-identifier',
        'DHIS2 FHIR Adapter Organization Identifier.');

INSERT INTO fhir_client (id, version, name, code, description, fhir_version, web_hook_authorization_header, dhis_authentication_method, dhis_username, dhis_password,
remote_base_url, use_remote, tolerance_millis, logging, verbose_logging, adapter_base_url, subscription_type, enabled, locked, use_adapter_identifier, exp_enabled)
VALUES ('a5a6a64215a24f279cee55a26a86d062', 0, 'FHIR REST Interfaces DSTU3', 'FHIR_RI_DSTU3', 'Used by the FHIR REST Interfaces for DSTU3.', 'DSTU3',
'None 1234', 'BASIC', 'none', 'none', 'http://localhost:8088/fhir', FALSE, 0, FALSE, FALSE,
'http://localhost:8080/fhiradapter', 'REST_HOOK_WITH_JSON_PAYLOAD', TRUE, FALSE, FALSE, TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('1de5bc4cb55e4be8b96ada239cfa2cf6', 0, 'a5a6a64215a24f279cee55a26a86d062', 'CONDITION', NULL, 'FHIR Condition Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('be9a7d61d5cd451a89f93e36cf213299', 0, 'a5a6a64215a24f279cee55a26a86d062', 'DIAGNOSTIC_REPORT', NULL, 'FHIR Diagnostic Report Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('edfde6aa8de9440bab7284a0f9be01e2', 0, 'a5a6a64215a24f279cee55a26a86d062', 'ENCOUNTER', NULL, 'FHIR Encounter Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('3e8719c1aef14728b6aab208dea629e2', 0, 'a5a6a64215a24f279cee55a26a86d062', 'IMMUNIZATION', NULL, 'FHIR Immunization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('8c4dc1b9d1da40698e8cab8409a3daf5', 0, 'a5a6a64215a24f279cee55a26a86d062', 'LOCATION', NULL, 'FHIR Location Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('00716d5d1c1e4ab9b860fcaccd7dad1d', 0, 'a5a6a64215a24f279cee55a26a86d062', 'MEDICATION_REQUEST', NULL, 'FHIR Medication Request Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('fade2919c46f472ab24de6e5bb99d5a6', 0, 'a5a6a64215a24f279cee55a26a86d062', 'OBSERVATION', NULL, 'FHIR Observation Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('7109e75f9ece47739ca56c8774827032', 0, 'a5a6a64215a24f279cee55a26a86d062', 'ORGANIZATION', NULL, 'FHIR Organization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('080f368d356649ddbedf6bb6026bdd28', 0, 'a5a6a64215a24f279cee55a26a86d062', 'PATIENT', NULL, 'FHIR Patient Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('3165533a0df143db8dd5ac57da8fb516', 0, 'a5a6a64215a24f279cee55a26a86d062', 'RELATED_PERSON', NULL, 'FHIR Related Person Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('c9ae7ad7ed40416080ca2964755b36ea', 0, 'a5a6a64215a24f279cee55a26a86d062', 'PRACTITIONER', NULL, 'FHIR Practitioner Resource.', TRUE);
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('99e36c20e8ef48d18d1fe80572384281', 0, 'a5a6a64215a24f279cee55a26a86d062', 'PATIENT', 'c8ca1fbd4d954911b16ea2310af3533f');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('bdf7381916934177bf6bca7a0c2c4ea9', 0, 'a5a6a64215a24f279cee55a26a86d062', 'LOCATION', 'cc46b8acaced4bd2b10403a7d34da438');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('2e56fc2b576a46caaa6d4485c75c6f5b', 0, 'a5a6a64215a24f279cee55a26a86d062', 'ORGANIZATION', 'db955a8aca584263bc56faa99085df93');

INSERT INTO fhir_client (id, version, name, code, description, fhir_version, web_hook_authorization_header, dhis_authentication_method, dhis_username, dhis_password,
remote_base_url, use_remote, tolerance_millis, logging, verbose_logging, adapter_base_url, subscription_type, enabled, locked, use_adapter_identifier, exp_enabled)
VALUES ('46f0af46365440b38d4c7a633332c3b3', 0, 'FHIR REST Interfaces R4', 'FHIR_RI_R4', 'Used by the FHIR REST Interfaces for R4.', 'R4',
'None 1234', 'BASIC', 'none', 'none', 'http://localhost:8088/fhir', FALSE, 0, FALSE, FALSE,
'http://localhost:8080/fhiradapter', 'REST_HOOK_WITH_JSON_PAYLOAD', FALSE, TRUE, FALSE, TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('fedf63ac80f04f24a8e9595767f0983a', 0, '46f0af46365440b38d4c7a633332c3b3', 'CONDITION', NULL, 'FHIR Condition Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('bf745a0b81a446299434c5715605d8e4', 0, '46f0af46365440b38d4c7a633332c3b3', 'DIAGNOSTIC_REPORT', NULL, 'FHIR Diagnostic Report Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('ef22502237624480a2ee702fa47283bc', 0, '46f0af46365440b38d4c7a633332c3b3', 'ENCOUNTER', NULL, 'FHIR Encounter Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('3ac34328e80e465b893bdf5afe7aa527', 0, '46f0af46365440b38d4c7a633332c3b3', 'IMMUNIZATION', NULL, 'FHIR Immunization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('2dc28bd3802d4cc18352e3b9277fd280', 0, '46f0af46365440b38d4c7a633332c3b3', 'LOCATION', NULL, 'FHIR Location Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('0e89f64e04af480e9c41e8423eb5c602', 0, '46f0af46365440b38d4c7a633332c3b3', 'MEDICATION_REQUEST', NULL, 'FHIR Medication Request Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('b8eccfd34ded45d7ab84dc8467541902', 0, '46f0af46365440b38d4c7a633332c3b3', 'OBSERVATION', NULL, 'FHIR Observation Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('f3491bb692794fb7a27560c217f88a6a', 0, '46f0af46365440b38d4c7a633332c3b3', 'ORGANIZATION', NULL, 'FHIR Organization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('6236c52d99614b9988f5d6e692021a7f', 0, '46f0af46365440b38d4c7a633332c3b3', 'PATIENT', NULL, 'FHIR Patient Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('c8d4991fc1004b9591c47b975ac636fd', 0, '46f0af46365440b38d4c7a633332c3b3', 'RELATED_PERSON', NULL, 'FHIR Related Person Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('26526d56224e46ecbc04827711f2b345', 0, '46f0af46365440b38d4c7a633332c3b3', 'PRACTITIONER', NULL, 'FHIR Practitioner Resource.', TRUE);
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('070a6e464dfb452e90c5475021532dc0', 0, '46f0af46365440b38d4c7a633332c3b3', 'PATIENT', 'c8ca1fbd4d954911b16ea2310af3533f');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('47fd1b287558429b85abc5f5fcc3d00f', 0, '46f0af46365440b38d4c7a633332c3b3', 'LOCATION', 'cc46b8acaced4bd2b10403a7d34da438');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('e2cdcfd609f044fe9dd2c3e97540ea22', 0, '46f0af46365440b38d4c7a633332c3b3', 'ORGANIZATION', 'db955a8aca584263bc56faa99085df93');

INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('dbc68848371346f78c5b5d0f12f091b9', 0, 'DHIS2 FHIR Adapter Condition Identifier', 'SYSTEM_DHIS2_FHIR_CONDITION_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/condition-identifier',
        'DHIS2 FHIR Adapter Condition Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('06bc5bb6116a4a759b677c64a29f99b8', 0, 'DHIS2 FHIR Adapter Diagnostic Report Identifier', 'SYSTEM_DHIS2_FHIR_DIAGNOSTIC_REPORT_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/diagnostic-report-identifier',
        'DHIS2 FHIR Adapter Diagnostic Report Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('84d4ee666bd24c31a0ead0cb1fcc0837', 0, 'DHIS2 FHIR Adapter Encounter Identifier', 'SYSTEM_DHIS2_FHIR_ENCOUNTER_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/encounteridentifier',
        'DHIS2 FHIR Adapter Organization Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('ce63a6d6a0054d648d0dd91c93c994fd', 0, 'DHIS2 FHIR Adapter Immunization Identifier', 'SYSTEM_DHIS2_FHIR_IMMUNIZATION_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/immunizationidentifier',
        'DHIS2 FHIR Adapter Immunization Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('36bfe5295e5f4f8186d1586e6ae76372', 0, 'DHIS2 FHIR Adapter Medication Request Identifier', 'SYSTEM_DHIS2_FHIR_MEDICATION_REQUEST_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/medicationrequestidentifier',
        'DHIS2 FHIR Adapter Medication Request Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('e10dca6e11714da1bc2d781ef2764aa8', 0, 'DHIS2 FHIR Adapter Observation Identifier', 'SYSTEM_DHIS2_FHIR_OBSERVATION_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/observationidentifier',
        'DHIS2 FHIR Adapter Observation Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('64beefd532404bcba40df1b61a62d27b', 0, 'DHIS2 FHIR Adapter Related Person Identifier', 'SYSTEM_DHIS2_FHIR_RELATED_PERSON_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/relatedpersonidentifier',
        'DHIS2 FHIR Adapter Related Person Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('77fd2a924761420b9db05b3999d8ef30', 0, 'DHIS2 FHIR Adapter Practitioner Identifier', 'SYSTEM_DHIS2_FHIR_PRACTITIONER_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/practitioneridentifier',
        'DHIS2 FHIR Adapter Practitioner Identifier.');

INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('f720eb06864545869510b01f8ce281a0', 0, 'a5a6a64215a24f279cee55a26a86d062', 'CONDITION', 'dbc68848371346f78c5b5d0f12f091b9');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('8f3bede5ca924bcdae3603c52f9aa2da', 0, 'a5a6a64215a24f279cee55a26a86d062', 'DIAGNOSTIC_REPORT', '06bc5bb6116a4a759b677c64a29f99b8');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('b19b210e98d1412db983af75366c2796', 0, 'a5a6a64215a24f279cee55a26a86d062', 'ENCOUNTER', '84d4ee666bd24c31a0ead0cb1fcc0837');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('a52778e9a00d44a7a6293cefd373ce11', 0, 'a5a6a64215a24f279cee55a26a86d062', 'IMMUNIZATION', 'ce63a6d6a0054d648d0dd91c93c994fd');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('92dd859b5933412385508455cd329394', 0, 'a5a6a64215a24f279cee55a26a86d062', 'MEDICATION_REQUEST', '36bfe5295e5f4f8186d1586e6ae76372');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('77d278dc01e1437e99c0e2b2e9bd78cd', 0, 'a5a6a64215a24f279cee55a26a86d062', 'OBSERVATION', 'e10dca6e11714da1bc2d781ef2764aa8');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('c99b20ff9854443bbbd297072e1f9453', 0, 'a5a6a64215a24f279cee55a26a86d062', 'RELATED_PERSON', '64beefd532404bcba40df1b61a62d27b');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('965fdc223b2e4a819762b8f8871ff0ee', 0, 'a5a6a64215a24f279cee55a26a86d062', 'PRACTITIONER', '77fd2a924761420b9db05b3999d8ef30');

INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('9271ae78a2c74630be2a2e141dbc4e7f', 0, '46f0af46365440b38d4c7a633332c3b3', 'CONDITION', 'dbc68848371346f78c5b5d0f12f091b9');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('d2c407a9d03e4140b4593909500372f4', 0, '46f0af46365440b38d4c7a633332c3b3', 'DIAGNOSTIC_REPORT', '06bc5bb6116a4a759b677c64a29f99b8');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('2dfbc117c31c4b41819de38624d8c262', 0, '46f0af46365440b38d4c7a633332c3b3', 'ENCOUNTER', '84d4ee666bd24c31a0ead0cb1fcc0837');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('c26387c482ac4a80b20ab8882e107590', 0, '46f0af46365440b38d4c7a633332c3b3', 'IMMUNIZATION', 'ce63a6d6a0054d648d0dd91c93c994fd');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('235aaaaa0f6d454a9de532b975329997', 0, '46f0af46365440b38d4c7a633332c3b3', 'MEDICATION_REQUEST', '36bfe5295e5f4f8186d1586e6ae76372');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('feceaac45b004f428e1bdd629f721cc9', 0, '46f0af46365440b38d4c7a633332c3b3', 'OBSERVATION', 'e10dca6e11714da1bc2d781ef2764aa8');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('5ae991206dd2483a8e6417858803f4e1', 0, '46f0af46365440b38d4c7a633332c3b3', 'RELATED_PERSON', '64beefd532404bcba40df1b61a62d27b');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('ccd13c8f2d794965b7be36d59561451d', 0, '46f0af46365440b38d4c7a633332c3b3', 'PRACTITIONER', '77fd2a924761420b9db05b3999d8ef30');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('f8faecad964f402c8cb4a8271085528d', 0, 'Returns if DHIS Org Unit is applicable for export', 'EXP_DHIS_ORG_UNIT_APPLICABLE',
        'Returns if the DHIS Org Unit is applicable for export.', 'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f8faecad964f402c8cb4a8271085528d', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f8faecad964f402c8cb4a8271085528d', 'INPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('6aaa5d0b5dd1423fbb3fdffc9f42f03f', 0, 'f8faecad964f402c8cb4a8271085528d',
'facilityLevel', 'INTEGER', TRUE, '4', 'Specifies the organization unit level in  which facilities are located.');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('ca71ef86a93e47d6ab2d645a977165a9', 0, 'f8faecad964f402c8cb4a8271085528d',
'input.getLevel() >= args[''facilityLevel'']', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('ca71ef86a93e47d6ab2d645a977165a9', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('ca71ef86a93e47d6ab2d645a977165a9', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a84850454d624b3a9cfd5c36760b8d45', 0, 'f8faecad964f402c8cb4a8271085528d',
        'Returns if DHIS Org Unit is applicable for export', 'EXP_DHIS_ORG_UNIT_APPLICABLE',
        'Returns if the DHIS Org Unit is applicable for export.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('b3568beb5a5d40599b4e3afa0157adbc', 0, 'Transforms DHIS Org Unit to FHIR Location', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_LOC',
        'Transforms DHIS Organization Unit to FHIR Organization.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_ORGANIZATION_UNIT', 'FHIR_LOCATION', 'f8faecad964f402c8cb4a8271085528d');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('b3568beb5a5d40599b4e3afa0157adbc', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('b3568beb5a5d40599b4e3afa0157adbc', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('4bb9745e43c0455fbeee98e95c83df3d', 0, 'b3568beb5a5d40599b4e3afa0157adbc',
'output.setManagingOrganization(null);
output.setOperationalStatus(null);
output.setPartOf(null);
output.setPhysicalType(null);
if (input.getParentId() != null)
{
  var parentOrganizationUnit = organizationUnitResolver.getMandatoryById(input.getParentId());
  var parentLocation = organizationUnitResolver.getFhirResource(parentOrganizationUnit);
  if (parentLocation == null)
  {
    context.missingDhisResource(parentOrganizationUnit.getResourceId());
  }
  output.getPartOf().setReferenceElement(parentLocation.getIdElement().toUnqualifiedVersionless());
}
if (input.getLevel() === args[''facilityLevel''])
{
  var organization = organizationUnitResolver.getFhirResource(input, ''ORGANIZATION'');
  if (organization == null)
  {
    context.missingDhisResource(input.getResourceId());
  }
  output.getManagingOrganization().setReferenceElement(organization.getIdElement().toUnqualifiedVersionless());
  output.getPhysicalType().addCoding().setSystem(''http://hl7.org/fhir/locationphysicaltype'').setCode(''si'').setDisplay(''Site'');
}
if (input.getLevel() < args[''facilityLevel''])
{
  output.getPhysicalType().addCoding().setSystem(''http://hl7.org/fhir/locationphysicaltype'').setCode(''jdn'').setDisplay(''Jurisdiction'');
}
if (input.getClosedDate() == null)
{
  output.setStatus(locationUtils.getLocationStatus(''active''));
}
else
{
  output.setStatus(locationUtils.getLocationStatus(''inactive''));
}
output.setPosition(geoUtils.createLocationPosition(input.getCoordinates()));
output.setName(input.getName());
output.getAlias().clear();
if ((input.getShortName() != null) && !input.getShortName().equals(input.getName()))
{
  output.addAlias(input.getShortName());
}
if ((input.getDisplayName() != null) && !input.getDisplayName().equals(input.getName()) && !fhirResourceUtils.containsString(output.getAlias(), input.getDisplayName()))
{
  output.addAlias(input.getDisplayName());
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4bb9745e43c0455fbeee98e95c83df3d', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4bb9745e43c0455fbeee98e95c83df3d', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description, base_executable_script_id)
VALUES ('b918b4cd67fc4d4d9b7491f98730acd7', 0, 'b3568beb5a5d40599b4e3afa0157adbc',
        'Transforms DHIS Org Unit to FHIR Location', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_LOC',
        'Transforms DHIS Organization Unit to FHIR Location.', 'a84850454d624b3a9cfd5c36760b8d45');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('7c350b43377942d6a9c974e567f6c066', 0, 'Returns FHIR Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER',
        'Returns the FHIR Identifier for the DHIS Org Unit.', 'EVALUATE', 'STRING', NULL, NULL);
UPDATE fhir_script SET base_script_id = (SELECT id FROM fhir_script WHERE id = 'a250e109a13542b28bdb1c050c1d384c')
WHERE id='7c350b43377942d6a9c974e567f6c066';
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('7c350b43377942d6a9c974e567f6c066', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('7c350b43377942d6a9c974e567f6c066', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('eca2e74dac8049bdbebf6b9129557567', 0, '7c350b43377942d6a9c974e567f6c066',
'var code = null;
if ((input.getCode() != null) && !input.getCode().isEmpty())
{
  code = codeUtils.getByMappedCode(input.getCode(), ''ORGANIZATION'');
  if ((code == null) && args[''useIdentifierCode''])
  {
    code = codeUtils.getCodeWithoutPrefix(input.getCode(), ''ORGANIZATION'');
  }
}
code', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('eca2e74dac8049bdbebf6b9129557567', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('eca2e74dac8049bdbebf6b9129557567', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('66d12e44471c4318827a0b397f694b6a', 0, '7c350b43377942d6a9c974e567f6c066',
        'Returns FHIR Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER',
        'Returns FHIR Identifier for the DHIS Org Unit.');
UPDATE fhir_executable_script SET base_executable_script_id = (SELECT id FROM fhir_executable_script WHERE id = '25a97bb47b394ed48677db4bcaa28ccf')
WHERE id='66d12e44471c4318827a0b397f694b6a';

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('655f55b538264fe7b38e1e29bcad09ec', 0, 'Returns FHIR Location Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER_LOC',
        'Returns the FHIR Location Identifier for the DHIS Org Unit.', 'EVALUATE', 'STRING', NULL, NULL);
UPDATE fhir_script SET base_script_id = (SELECT id FROM fhir_script WHERE id = 'a250e109a13542b28bdb1c050c1d384c')
WHERE id='655f55b538264fe7b38e1e29bcad09ec';
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('655f55b538264fe7b38e1e29bcad09ec', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('655f55b538264fe7b38e1e29bcad09ec', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('1a2bf7fc290841488a535edde593a4e9', 0, '655f55b538264fe7b38e1e29bcad09ec',
'var code = null;
if ((input.getCode() != null) && !input.getCode().isEmpty())
{
  code = codeUtils.getByMappedCode(input.getCode(), ''LOCATION'');
  if ((code == null) && args[''useIdentifierCode''])
  {
    code = codeUtils.getCodeWithoutPrefix(input.getCode(), ''LOCATION'');
  }
}
code', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('1a2bf7fc290841488a535edde593a4e9', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('1a2bf7fc290841488a535edde593a4e9', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('5090c485a2254c2ea8f695fa74ce1618', 0, '655f55b538264fe7b38e1e29bcad09ec',
        'Returns FHIR Location Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER_LOC',
        'Returns FHIR Location Identifier for the DHIS Org Unit.');
UPDATE fhir_executable_script SET base_executable_script_id = (SELECT id FROM fhir_executable_script WHERE id = '25a97bb47b394ed48677db4bcaa28ccf')
WHERE id='5090c485a2254c2ea8f695fa74ce1618';

INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, applicable_exp_script_id, transform_exp_script_id, exp_enabled, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, simple_fhir_id)
VALUES ('b9546b024adc4868a4cdd5d7789f0df0', 0, 'DHIS Organization Unit to FHIR Location', NULL, TRUE, 1, 'LOCATION', 'ORGANIZATION_UNIT', FALSE, NULL, 'b918b4cd67fc4d4d9b7491f98730acd7', true, true, true, true, true);
INSERT INTO fhir_organization_unit_rule(id, identifier_lookup_script_id, mo_identifier_lookup_script_id) VALUES ('b9546b024adc4868a4cdd5d7789f0df0', '5090c485a2254c2ea8f695fa74ce1618', '66d12e44471c4318827a0b397f694b6a');




INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('53fdf1da61454e259d25fc41b9baf09f', 0, 'Transforms DHIS Org Unit to FHIR Organization', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_ORG',
        'Transforms DHIS Organization Unit to FHIR Organization.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_ORGANIZATION_UNIT', 'FHIR_ORGANIZATION');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('53fdf1da61454e259d25fc41b9baf09f', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('53fdf1da61454e259d25fc41b9baf09f', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('50544af8cf524e1e9a5dd44c736bc8d8', 0, '53fdf1da61454e259d25fc41b9baf09f',
'output.setPartOf(null);
output.getType().clear();
if ((input.getLevel() > args[''facilityLevel'']) && (input.getParentId() != null))
{
  var parentOrganizationUnit = organizationUnitResolver.getMandatoryById(input.getParentId());
  var parentOrganization = organizationUnitResolver.getFhirResource(parentOrganizationUnit);
  if (parentOrganization == null)
  {
    context.missingDhisResource(parentOrganizationUnit.getResourceId());
  }
  output.getPartOf().setReferenceElement(parentOrganization.getIdElement().toUnqualifiedVersionless());
}
else
{
  output.addType().addCoding().setSystem(''http://hl7.org/fhir/organizationtype'').setCode(''prov'');
}
output.setActive(input.getClosedDate() == null);
output.setName(input.getName());
output.getAlias().clear();
if ((input.getShortName() != null) && !input.getShortName().equals(input.getName()))
{
  output.addAlias(input.getShortName());
}
if ((input.getDisplayName() != null) && !input.getDisplayName().equals(input.getName()) && !fhirResourceUtils.containsString(output.getAlias(), input.getDisplayName()))
{
  output.addAlias(input.getDisplayName());
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('50544af8cf524e1e9a5dd44c736bc8d8', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('50544af8cf524e1e9a5dd44c736bc8d8', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('50544af8cf524e1e9a5dd44c736bc8d8', 0, '53fdf1da61454e259d25fc41b9baf09f',
        'Transforms DHIS Org Unit to FHIR Organization', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_ORG',
        'Transforms DHIS Organization Unit to FHIR Organization.');

INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, applicable_exp_script_id, transform_exp_script_id, exp_enabled, fhir_create_enabled, fhir_update_enabled,
fhir_delete_enabled, simple_fhir_id)
VALUES ('d0e1472a05e647c9b36bff1f06fec352', 0, 'DHIS Organization Unit to FHIR Organization', NULL, TRUE, 0, 'ORGANIZATION', 'ORGANIZATION_UNIT', FALSE, 'a84850454d624b3a9cfd5c36760b8d45', '50544af8cf524e1e9a5dd44c736bc8d8', TRUE, TRUE, TRUE, TRUE, TRUE);
INSERT INTO fhir_organization_unit_rule(id, identifier_lookup_script_id) VALUES ('d0e1472a05e647c9b36bff1f06fec352', '66d12e44471c4318827a0b397f694b6a');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('cf3072ec06ad4d62a8a075ad2ab330ba', 0, 'SEARCH_FILTER_LOCATION', 'Prepares Location Search Filter', 'Prepares Location Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('cf3072ec06ad4d62a8a075ad2ab330ba', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('10200cc2f0604aa8a9070d12b199ff3b', 0, 'cf3072ec06ad4d62a8a075ad2ab330ba',
'searchFilter.add(''name'', ''string'', ''name'');
searchFilter.addReference(''partof'', ''location'', ''organization-unit'', ''parent.id'');
searchFilter.addToken(''status'', ''active'', ''closedDate'', ''null'', null);
searchFilter.addToken(''status'', ''inactive'', ''closedDate'', ''!null'', null);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('10200cc2f0604aa8a9070d12b199ff3b', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('10200cc2f0604aa8a9070d12b199ff3b', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('27f50aeb0f564256ae166118e931524b', 0, 'cf3072ec06ad4d62a8a075ad2ab330ba', 'Prepares Location Search Filter', 'SEARCH_FILTER_LOCATION');
UPDATE fhir_rule SET filter_script_id='27f50aeb0f564256ae166118e931524b' WHERE id='b9546b024adc4868a4cdd5d7789f0df0';

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('2f3b01d641ce41eaa66ccc7b1d98aea6', 0, 'Transforms DHIS Person to FHIR Patient', 'TRANSFORM_DHIS_PERSON_FHIR_PATIENT', 'Transforms DHIS Person to FHIR Patient.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_TRACKED_ENTITY_INSTANCE', 'FHIR_PATIENT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('2f3b01d641ce41eaa66ccc7b1d98aea6', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('2f3b01d641ce41eaa66ccc7b1d98aea6', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('2f3b01d641ce41eaa66ccc7b1d98aea6', 'OUTPUT');
UPDATE fhir_script SET base_script_id = (SELECT id FROM fhir_script WHERE id = 'ea8879435e944e319441c7661fe1063e')
WHERE id='2f3b01d641ce41eaa66ccc7b1d98aea6';
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('d7e622c519144e5ba590039fc0c2a105', 0, '2f3b01d641ce41eaa66ccc7b1d98aea6',
'resetFhirValue', 'BOOLEAN', TRUE, 'false', 'Specifies if existing values in FHIR can be reset by null values (except first and last name).');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('6f8864e7cd4947f89756556e92955c39', 0, '2f3b01d641ce41eaa66ccc7b1d98aea6',
'resetAddressText', 'BOOLEAN', TRUE, 'false', 'Specifies if existing address values (other than text representation) may be reset.');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('f0a48e63cc1d4d0285fa80c7e79a5d9e', 0, '2f3b01d641ce41eaa66ccc7b1d98aea6',
'function canOverrideAddress(address)
{
  return !address.hasLine() && !address.hasCity() && !address.hasDistrict() && !address.hasState() && !address.hasPostalCode() && !address.hasCountry();
}
if (output.getName().size() < 2)
{
  var lastName = input.getValue(args[''lastNameAttribute'']);
  var firstName = input.getValue(args[''firstNameAttribute'']);
  if ((lastName != null) || (firstName != null) || args[''resetFhirValue''])
  {
    output.getName().clear();
    if ((lastName != null) || args[''resetFhirValue''])
    {
      output.getNameFirstRep().setFamily(lastName);
    }
    if ((firstName != null) || args[''resetFhirValue''])
    {
      humanNameUtils.updateGiven(output.getNameFirstRep(), firstName);
    }
  }
}
if (args[''birthDateAttribute''] != null)
{
  var birthDate = input.getValue(args[''birthDateAttribute'']);
  if ((birthDate != null) || args[''resetFhirValue''])
  {
    output.setBirthDateElement(dateTimeUtils.getPreciseDateElement(birthDate));
  }
}
if (args[''genderAttribute''] != null)
{
  var gender = input.getValue(args[''genderAttribute'']);
  if ((gender != null) || args[''resetFhirValue''])
  {
    output.setGender(genderUtils.getAdministrativeGender(gender));
  }
}
if ((args[''addressTextAttribute''] != null) && (output.getAddress().size() < 2))
{
  var addressText = input.getValue(args[''addressTextAttribute'']);
  if (((addressText != null) || args[''resetFhirValue'']) && (args[''resetAddressText''] || !output.hasAddress() || canOverrideAddress(output.getAddressFirstRep())))
  {
    output.getAddress().clear();
    output.addAddress().setText(addressText);
  }
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('f0a48e63cc1d4d0285fa80c7e79a5d9e', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('f0a48e63cc1d4d0285fa80c7e79a5d9e', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('6a014e536a7e4bb68cfc817e4756ed4d', 0, '2f3b01d641ce41eaa66ccc7b1d98aea6',
        'Transforms DHIS Person to FHIR Patient', 'TRANSFORM_DHIS_PERSON_FHIR_PATIENT', 'Transforms DHIS Person to FHIR Patient.');
UPDATE fhir_executable_script SET base_executable_script_id = (SELECT id FROM fhir_executable_script WHERE id = '72451c8f7492470790b8a3e0796de19e')
WHERE id='6a014e536a7e4bb68cfc817e4756ed4d';

UPDATE fhir_rule
SET transform_exp_script_id = '6a014e536a7e4bb68cfc817e4756ed4d'
WHERE id = '5f9ebdc9852e4c8387ca795946aabc35';

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('4318ff25f09f414baa781958e47eca09', 0, 'Transforms DHIS Person GEO data to FHIR Patient', 'TRANSFORM_DHIS_PERSON_FHIR_PATIENT_GEO', 'Transforms the coordinates that are includes in DHIS Person to FHIR Patient.', 'TRANSFORM_TO_FHIR',
        'BOOLEAN', 'DHIS_TRACKED_ENTITY_INSTANCE', 'FHIR_PATIENT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('4318ff25f09f414baa781958e47eca09', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('4318ff25f09f414baa781958e47eca09', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('4318ff25f09f414baa781958e47eca09', 'OUTPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('650e2d2582d7450abac03a20f31817b2', 0, '4318ff25f09f414baa781958e47eca09',
'if (output.getAddress().size() < 2)
{
  geoUtils.updateAddress(output.getAddressFirstRep(), input.getCoordinates());
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('650e2d2582d7450abac03a20f31817b2', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('650e2d2582d7450abac03a20f31817b2', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('b9d24e63d1ee43a1968a77fbc3f805fb', 0, '4318ff25f09f414baa781958e47eca09',
        'Transforms DHIS Person GEO data to FHIR Patient', 'TRANSFORM_DHIS_PERSON_FHIR_PATIENT_GEO',
        'Transforms the coordinates that are includes in DHIS Person to FHIR Patient.');

UPDATE fhir_tracked_entity_rule
SET exp_geo_transform_script_id = 'b9d24e63d1ee43a1968a77fbc3f805fb'
WHERE id = '5f9ebdc9852e4c8387ca795946aabc35';

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('ae61e5bb51c54740bc35b2d054eff202', 0, 'Transforms DHIS Org Unit to FHIR Resource', 'TRANSFORM_DHIS_OU_TO_FHIR', 'Transforms the DHIS Organization Unit to the FHIR resource.', 'TRANSFORM_TO_FHIR',
        'BOOLEAN', NULL, NULL);
UPDATE fhir_script SET base_script_id = (SELECT id FROM fhir_script WHERE id = 'a250e109a13542b28bdb1c050c1d384c')
WHERE id='ae61e5bb51c54740bc35b2d054eff202';
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('ae61e5bb51c54740bc35b2d054eff202', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('ae61e5bb51c54740bc35b2d054eff202', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('ae61e5bb51c54740bc35b2d054eff202', 'OUTPUT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('ae61e5bb51c54740bc35b2d054eff202', 'ORGANIZATION_UNIT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('b7a87ddde968465d874cceeff61b7cd8', 0, 'ae61e5bb51c54740bc35b2d054eff202',
'overrideExisting', 'BOOLEAN', TRUE, 'true', 'Specifies if an existing value should be overridden.');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('78c2b73c469c4ab4824407e817b72d4a', 0, 'ae61e5bb51c54740bc35b2d054eff202',
'var ok = false;
var code = null;
if (output.managingOrganization && (output.getManagingOrganization().isEmpty() || args[''overrideExisting'']))
{
  if ((organizationUnit == null) || (organizationUnit.getCode() == null) || organizationUnit.getCode().isEmpty())
  {
    output.setManagingOrganization(null);
  }
  else
  {
    code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''ORGANIZATION'');
    if ((code == null) && args[''useIdentifierCode''])
    {
      code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''ORGANIZATION'');
    }
  }
}
else
{
  ok = true;
}
if (code != null)
{
  var organization = fhirClientUtils.findBySystemIdentifier(''ORGANIZATION'', code);
  if (organization == null)
  {
    context.missingDhisResource(organizationUnit.getResourceId());
  }
  output.setManagingOrganization(null);
  output.getManagingOrganization().setReferenceElement(organization.getIdElement().toUnqualifiedVersionless());
  ok = true;
}
ok', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('78c2b73c469c4ab4824407e817b72d4a', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('78c2b73c469c4ab4824407e817b72d4a', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('416decee4604473ab6501a997d731ff0', 0, 'ae61e5bb51c54740bc35b2d054eff202',
        'Transforms DHIS Org Unit to FHIR Resource', 'TRANSFORM_DHIS_OU_TO_FHIR',
        'Transforms the DHIS Organization Unit to the FHIR resource.');
UPDATE fhir_executable_script SET base_executable_script_id = (SELECT id FROM fhir_executable_script WHERE id = '25a97bb47b394ed48677db4bcaa28ccf')
WHERE id='416decee4604473ab6501a997d731ff0';

UPDATE fhir_tracked_entity_rule
SET exp_ou_transform_script_id = '416decee4604473ab6501a997d731ff0'
WHERE id = '5f9ebdc9852e4c8387ca795946aabc35';

UPDATE fhir_rule SET exp_enabled=TRUE
WHERE id IN(
  '52227dd9c79c478b92af9aa1f33c76fd',
  '5f9ebdc9852e4c8387ca795946aabc35',
  'd0e1472a05e647c9b36bff1f06fec352',
  'b9546b024adc4868a4cdd5d7789f0df0');
UPDATE fhir_tracked_entity SET exp_enabled=TRUE
WHERE id IN ('4203754d21774a4486aa2de31ee4c8ee');

UPDATE fhir_script_source SET source_text=
'var ok = false;
var code = null;
if (output.location)
{
  if (context.getDhisRequest().isDhisFhirId())
  {
    var ref = context.getDhisFhirResourceReference(organizationUnit, ''Location'');
    if (ref != null)
    {
      if (typeof output.addLocation === ''undefined'')
      {
        output.setLocation(ref);
      }
      else
      {
        output.addLocation().setLocation(ref);
      }
      ok = true;
    }
  }
  else if (!output.getLocation().isEmpty() && !args[''overrideExisting''])
  {
    ok = true;
  }
  else if ((organizationUnit == null) || (organizationUnit.getCode() == null) || organizationUnit.getCode().isEmpty())
  {
    output.setLocation(null);
    ok = true;
  }
  else
  {
    code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''LOCATION'');
    if ((code == null) && args[''useLocationIdentifierCode''])
    {
      code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''LOCATION'');
    }
  }
  if (code != null)
  {
    var resource = fhirClientUtils.findBySystemIdentifier(''LOCATION'', code);
    if (resource == null)
    {
      context.missingDhisResource(organizationUnit.getResourceId());
    }
    output.setLocation(null);
    if (typeof output.addLocation !== ''undefined'')
    {
      output.addLocation().getLocation().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
    }
    else
    {
      output.getLocation().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
    }
    ok = true;
  }
}
if (!ok && (output.managingOrganization || output.performer || output.serviceProvider))
{
  if (context.getDhisRequest().isDhisFhirId())
  {
    var ref = context.getDhisFhirResourceReference(organizationUnit, ''Organization'');
    if (ref != null)
    {
      if (output.managingOrganization)
      {
        output.setManagingOrganization(ref);
        ok = true;
      }
      else if (output.performer)
      {
        output.setPerformer(null);
        var performer = output.addPerformer();
        if (typeof performer.actor === ''undefined'')
        {
          performer.setReferenceElement(ref.getReferenceElement());
        }
        else
        {
          performer.setActor(ref);
        }
        ok = true;
      }
      else if (output.serviceProvider)
      {
        output.setServiceProvider(ref);
        ok = true;
      }
    }
  }
  else if (((output.managingOrganization && !output.getManagingOrganization().isEmpty()) || (output.performer && !output.getPerformer().isEmpty()) || (output.serviceProvider && !output.getServiceProvider().isEmpty())) && !args[''overrideExisting''])
  {
    ok = true;
  }
  else if ((organizationUnit == null) || (organizationUnit.getCode() == null) || organizationUnit.getCode().isEmpty())
  {
    if (output.managingOrganization)
    {
      output.setManagingOrganization(null);
      ok = true;
    }
    else if (output.performer)
    {
      output.setPerformer(null);
      ok = true;
    }
    else if (output.serviceProvider)
    {
      output.setServiceProvider(null);
      ok = true;
    }
  }
  else
  {
    code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''ORGANIZATION'');
    if ((code == null) && args[''useIdentifierCode''])
    {
      code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''ORGANIZATION'');
    }
  }
  if (code != null)
  {
    var resource = fhirClientUtils.findBySystemIdentifier(''ORGANIZATION'', code);
    if (resource == null)
    {
      context.missingDhisResource(organizationUnit.getResourceId());
    }
    if (output.managingOrganization)
    {
      output.setManagingOrganization(null);
      output.getManagingOrganization().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      ok = true;
    }
    else if (output.performer)
    {
      output.setPerformer(null);
      var performer = output.addPerformer();
      if (typeof performer.actor === ''undefined'')
      {
        performer.setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      }
      else
      {
        performer.getActor().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      }
      ok = true;
    }
    else if (output.serviceProvider)
    {
      output.setServiceProvider(null);
      output.getServiceProvider().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      ok = true;
    }
  }
}
ok' WHERE id = '78c2b73c469c4ab4824407e817b72d4a';

UPDATE fhir_script_source SET source_text =
'output.setPartOf(null);
output.getType().clear();
if ((input.getLevel() > args[''facilityLevel'']) && (input.getParentId() != null))
{
  if (context.getDhisRequest().isIncludeReferences())
  {
    var parentOrganizationUnit = organizationUnitResolver.getMandatoryById(input.getParentId());
    var parentOrganizationRef = null;
    if (context.getDhisRequest().isDhisFhirId())
    {
      parentOrganizationRef = context.getDhisFhirResourceReference(parentOrganizationUnit, ''Organization'');
    }
    else
    {
      var parentOrganization = organizationUnitResolver.getFhirResource(parentOrganizationUnit);
      if (parentOrganization == null)
      {
        context.missingDhisResource(parentOrganizationUnit.getResourceId());
      }
      parentOrganizationRef = fhirResourceUtils.createReference(parentOrganization);
    }
    output.setPartOf(parentOrganizationRef);
  }
}
else
{
  output.addType().setText(args[''facilityTypeDisplayName'']).addCoding().setSystem(''http://hl7.org/fhir/organization-type'').setCode(''prov'');
}
output.setActive(input.getClosedDate() == null);
output.setName(input.getName());
output.getAlias().clear();
if ((input.getShortName() != null) && !input.getShortName().equals(input.getName()))
{
  output.addAlias(input.getShortName());
}
if ((input.getDisplayName() != null) && !input.getDisplayName().equals(input.getName()) && !fhirResourceUtils.containsString(output.getAlias(), input.getDisplayName()))
{
  output.addAlias(input.getDisplayName());
}
true' WHERE id='50544af8cf524e1e9a5dd44c736bc8d8' AND version=0;

UPDATE fhir_script_source SET source_text=
'output.setManagingOrganization(null);
output.setOperationalStatus(null);
output.setPartOf(null);
output.setPhysicalType(null);
if (input.getParentId() != null)
{
  if (context.getDhisRequest().isIncludeReferences())
  {
    var parentOrganizationUnit = organizationUnitResolver.getMandatoryById(input.getParentId());
    var parentLocationRef = null;
    if (context.getDhisRequest().isDhisFhirId())
    {
      parentLocationRef = context.getDhisFhirResourceReference(parentOrganizationUnit, ''Location'');
    }
    else
    {
      var parentLocation = organizationUnitResolver.getFhirResource(parentOrganizationUnit);
      if (parentLocation == null)
      {
        context.missingDhisResource(parentOrganizationUnit.getResourceId());
      }
      parentLocationRef = fhirResourceUtils.createReference(parentLocation);
    }
    output.setPartOf(parentLocationRef);
  }
}
if (input.getLevel() === args[''facilityLevel''])
{
  if (context.getDhisRequest().isIncludeReferences())
  {
    var organizationRef = null;
    if (context.getDhisRequest().isDhisFhirId())
    {
      organizationRef = context.getDhisFhirResourceReference(input, ''Organization'');
    }
    else
    {
      var organization = organizationUnitResolver.getFhirResource(input, ''ORGANIZATION'');
      if (organization == null)
      {
        context.missingDhisResource(input.getResourceId());
      }
      organizationRef = fhirResourceUtils.createReference(organization);
    }
    output.setManagingOrganization(organizationRef);
  }
  output.getPhysicalType().addCoding().setSystem(''http://hl7.org/fhir/location-physical-type'').setCode(''si'').setDisplay(''Site'');
}
if (input.getLevel() < args[''facilityLevel''])
{
  output.getPhysicalType().addCoding().setSystem(''http://hl7.org/fhir/location-physical-type'').setCode(''jdn'').setDisplay(''Jurisdiction'');
}
if (input.getClosedDate() == null)
{
  output.setStatus(locationUtils.getLocationStatus(''active''));
}
else
{
  output.setStatus(locationUtils.getLocationStatus(''inactive''));
}
output.setPosition(geoUtils.createLocationPosition(input.getCoordinates()));
output.setName(input.getName());
output.getAlias().clear();
if ((input.getShortName() != null) && !input.getShortName().equals(input.getName()))
{
  output.addAlias(input.getShortName());
}
if ((input.getDisplayName() != null) && !input.getDisplayName().equals(input.getName()) && !fhirResourceUtils.containsString(output.getAlias(), input.getDisplayName()))
{
  output.addAlias(input.getDisplayName());
}
true' WHERE id='4bb9745e43c0455fbeee98e95c83df3d' AND version=0;

INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('1197b27e395643dda75cbfc6808dc49d', 0, 'Vital Sign', 'VITAL_SIGN', 'Vital signs.');
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('d4aa72e9b57e4d5c8856860ebdf460af', 0, '1197b27e395643dda75cbfc6808dc49d', 'All Birth Weight Observations', 'ALL_OB_BIRTH_WEIGHT', 'All Birth Weight Observations.');
-- Code set with all Body Weight Observations
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('d37dfecbce884fa49a7844ffe874c140', 0, '1197b27e395643dda75cbfc6808dc49d', 'All Body Weight Observations', 'ALL_OB_BODY_WEIGHT', 'All Body Weight Observations.');

INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('0d78f5e3c9fd48599768fb7c898a4142', 0, '1197b27e395643dda75cbfc6808dc49d', 'Weight Measured', 'LOINC_3141-9', '3141-9 / Body weight / Mass / Pt / ^Patient / Qn / Measured / ACTIVE / Weight Measured / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('a748a35e00e64926abc68cde3bb30ee4', 0, '1197b27e395643dda75cbfc6808dc49d', 'Weight', 'LOINC_29463-7', '29463-7 / Body weight / Mass / Pt / ^Patient / Qn /  / ACTIVE / Weight / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('26bb2fe18c804f01bfa2ccca24ffabf7', 0, '1197b27e395643dda75cbfc6808dc49d', 'Weight usual Reported', 'LOINC_75292-3', '75292-3 / Body weight^usual / Mass / Pt / ^Patient / Qn / Reported / ACTIVE / Weight usual Reported / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('34ab9dc24e284e2d8dcb99c78d24b0fa', 0, '1197b27e395643dda75cbfc6808dc49d', 'Weight W clothes Measured', 'LOINC_8350-1', '8350-1 / Body weight^with clothes / Mass / Pt / ^Patient / Qn / Measured / ACTIVE / Weight W clothes Measured / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('4b3d1fb02a664da4a8e54e3ddcf8cf8c', 0, '1197b27e395643dda75cbfc6808dc49d', 'Weight WO clothes Measured', 'LOINC_8351-9', '8351-9 / Body weight^without clothes / Mass / Pt / ^Patient / Qn / Measured / ACTIVE / Weight WO clothes Measured / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('c03c087818964d058a6c593892bdd5ba', 0, '1197b27e395643dda75cbfc6808dc49d', 'Weight Est', 'LOINC_8335-2', '8335-2 / Body weight / Mass / Pt / ^Patient / Qn / Estimated / ACTIVE / Weight Est / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('0139aaeec9884ba8838cbc26331a9c96', 0, '1197b27e395643dda75cbfc6808dc49d', 'Weight Stated', 'LOINC_3142-7', '3142-7 / Body weight / Mass / Pt / ^Patient / Qn / Stated / ACTIVE / Weight Stated / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('124b0bb5d7cc483ba555356fd3651b4f', 0, '1197b27e395643dda75cbfc6808dc49d', 'Birth weight GNWCH', 'LOINC_56092-0', '56092-0 / Body weight^at birth / Mass / Pt / ^Patient / Qn / GNWCH / ACTIVE / Birth weight GNWCH / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('bcdc2c1830ee48609b792f810cbcb0f1', 0, '1197b27e395643dda75cbfc6808dc49d', 'Birth weight Measured', 'LOINC_8339-4', '8339-4 / Body weight^at birth / Mass / Pt / ^Patient / Qn / Measured / ACTIVE / Birth weight Measured / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('160ab849ec0142d381dc056f28faf14d', 0, '1197b27e395643dda75cbfc6808dc49d', 'Birth weight NVSS', 'LOINC_56093-8', '56093-8 / Body weight^at birth / Mass / Pt / ^Patient / Qn / NVSS / ACTIVE / Birth weight NVSS / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('644b6a02160d4021b379dcd8d8044684', 0, '1197b27e395643dda75cbfc6808dc49d', 'Birth weight Reported', 'LOINC_56056-5', '56056-5 / Body weight^at birth / Mass / Pt / ^Patient / Qn / Reported / ACTIVE / Birth weight Reported / LOINC®');

INSERT INTO fhir_code_set_value(id, code_set_id, code_id, enabled)
  SELECT uuid(), 'd37dfecbce884fa49a7844ffe874c140', id, true FROM fhir_code WHERE code IN
  ('LOINC_3141-9', 'LOINC_29463-7', 'LOINC_75292-3', 'LOINC_8350-1', 'LOINC_8351-9', 'LOINC_8335-2', 'LOINC_3142-7');

INSERT INTO fhir_system (id, version, name, code, system_uri, description_protected, description)
VALUES ('f6e720a2e9ff43a8a2fdd5636106297b', 0, 'LOINC', 'SYSTEM_LOINC', 'http://loinc.org', TRUE,
'Available at http://loinc.org/, the terms of use apply to all the codes and filterings/mappings (i.e. rules and transformations) that are associated with this system. This copyright notice must neither be changed nor removed.
This content LOINC® is copyright © 1995 Regenstrief Institute, Inc. and the LOINC Committee, and available at no cost under the license at http://loinc.org/terms-of-use.');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code, display_name, system_code_value)
VALUES ('a80b6643580c468f8a76a11a16974cea', 0, '26bb2fe18c804f01bfa2ccca24ffabf7', 'f6e720a2e9ff43a8a2fdd5636106297b', '75292-3', 'Body weight', 'http://loinc.org|75292-3');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code, display_name, system_code_value)
VALUES ('d88ff0fe6f76456e978594859bd63069', 0, 'a748a35e00e64926abc68cde3bb30ee4', 'f6e720a2e9ff43a8a2fdd5636106297b', '29463-7', 'Body weight', 'http://loinc.org|29463-7');

INSERT INTO fhir_script (id, version, created_at, last_updated_at, last_updated_by, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('f1da6937e2fe47a4b0f38bbff7818ee1', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'TRANSFORM_FHIR_OB_BODY_WEIGHT', 'Transforms FHIR Observation Body Weight', 'Transforms FHIR Observation Body Weight to a data element and performs weight unit conversion.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_OBSERVATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f1da6937e2fe47a4b0f38bbff7818ee1', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f1da6937e2fe47a4b0f38bbff7818ee1', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f1da6937e2fe47a4b0f38bbff7818ee1', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('0767919959ae45309411ac5814102372', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f1da6937e2fe47a4b0f38bbff7818ee1',
'dataElement', 'DATA_ELEMENT_REF', TRUE, FALSE, NULL, 'Data element on which the body weight must be set.');
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('1ef4f760de9a4c29a321a8eee5c52313', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f1da6937e2fe47a4b0f38bbff7818ee1',
'override', 'BOOLEAN', TRUE, FALSE, 'true', 'Specifies if an existing value should be overridden.');
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('d8cd0e7d778045d18094b448b480e6b8', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f1da6937e2fe47a4b0f38bbff7818ee1',
'round', 'BOOLEAN', TRUE, FALSE, 'true', 'Specifies if the resulting value should be rounded.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('3d15bf81343c45bc9c281e87a8da6fa5', 0, 'f1da6937e2fe47a4b0f38bbff7818ee1',
'weightUnit', 'WEIGHT_UNIT', TRUE, 'KILO_GRAM', 'The resulting weight unit in which the value will be set on the data element.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('8d26fe6ad9ac40ababc85a87ab340762', 0, 'f1da6937e2fe47a4b0f38bbff7818ee1',
'output.setValue(args[''dataElement''], vitalSignUtils.getWeight(input.value, args[''weightUnit''], args[''round'']), null, args[''override''], context.getFhirRequest().getLastUpdated())', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('8d26fe6ad9ac40ababc85a87ab340762', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('8d26fe6ad9ac40ababc85a87ab340762', 'R4');

-- Script that is executed to check if enrollment into Child Programme is applicable
INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('3ddfc83e065546db81110a3370970125', 0, 'Child Programme Creation Applicable', 'CHILD_PROGRAMME_CREATION_APPLICABLE',
'Checks if the enrollment into child programme is applicable. The enrollment is applicable if the person is younger than a defined amount time.',
'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('3ddfc83e065546db81110a3370970125', 'TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('3ddfc83e065546db81110a3370970125', 'DATE_TIME');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('b06c9036006b40439c6f57dd7286098b', 0, '3ddfc83e065546db81110a3370970125',
'birthDateAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, FALSE, 'CODE:MMD_PER_DOB', 'The reference of the tracked entity attribute that contains the birth date of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('8cddd857d510472c835ad9454ffe1d39', 0, '3ddfc83e065546db81110a3370970125',
'age', 'INTEGER', TRUE, FALSE, '1', 'The person must be younger than the this amount of time (in specified units).');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('331db7c9edd84ff59e7740c391aa789e', 0, '3ddfc83e065546db81110a3370970125',
'ageUnit', 'DATE_UNIT', TRUE, FALSE, 'YEARS', 'The unit in which the age is specified.');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('1f9dc84d29a544609192eb03cea156d9', 0, '3ddfc83e065546db81110a3370970125', 'dateTimeUtils.isYoungerThan(dateTime, trackedEntityInstance.getValue(args[''birthDateAttribute'']), args[''age''], args[''ageUnit''])', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('1f9dc84d29a544609192eb03cea156d9', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('1f9dc84d29a544609192eb03cea156d9', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('dfb7f13cae214cfb88156f1416ca8388', 0, '3ddfc83e065546db81110a3370970125',
'Child Programme Creation Applicable', 'CHILD_PROGRAMME_CREATION_APPLICABLE', 'Checks if the enrollment of a person is applicable.');

-- Script that is executed on creation of a program instance of Child Programme
INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('60e83e185f6640a19c9d1d993e5ccdb3', 0, 'Child Programme Creation', 'CHILD_PROGRAMME_CREATION', 'Child programme creation.', 'EVALUATE', 'BOOLEAN', NULL, 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('60e83e185f6640a19c9d1d993e5ccdb3', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('60e83e185f6640a19c9d1d993e5ccdb3', 'ENROLLMENT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('363ed415be4a4311a7bf780a304d6f8c', 0, '60e83e185f6640a19c9d1d993e5ccdb3',
'birthDateAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, FALSE, 'CODE:MMD_PER_DOB', 'The reference of the tracked entity attribute that contains the birth date of the Person.');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('f45aed605208430e869ef0b87f5a6321', 0, '60e83e185f6640a19c9d1d993e5ccdb3', 'enrollment.setIncidentDate(trackedEntityInstance.getValue(args[''birthDateAttribute'']))', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('f45aed605208430e869ef0b87f5a6321', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('f45aed605208430e869ef0b87f5a6321', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('6836cbd193ba4ab8a4cb69348ad9d099', 0, '60e83e185f6640a19c9d1d993e5ccdb3',
'Child Programme Creation', 'CHILD_PROGRAMME_CREATION', 'Child programme creation.');

-- Tracker Program Child Programme, Birth: Weight from Body Weight
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('4a326b8949614b6980218d5285c2b0a7', 0, 'f1da6937e2fe47a4b0f38bbff7818ee1', 'CP: Birth Weight from Body Weight', 'CP_BIRTH_WEIGHT_BODY_WEIGHT');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('0c2062a7c136472aa38894ce6b8325f2', '4a326b8949614b6980218d5285c2b0a7', '1ef4f760de9a4c29a321a8eee5c52313', 'true');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('70684c93b22a4fff86bb604683c27fdd', '4a326b8949614b6980218d5285c2b0a7', '0767919959ae45309411ac5814102372', 'CODE:DE_2005736');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('6e29a09d94f848b6863e976fc3c58568', '4a326b8949614b6980218d5285c2b0a7', '3d15bf81343c45bc9c281e87a8da6fa5', 'GRAM');

-- Script that creates an observation with the the body weight from a data element with a specific weight unit
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('83a84beeeadc4c8ab78f8c8b9269883d', 0, 'TRANSFORM_BODY_WEIGHT_FHIR_OB', 'Transforms Body Weight FHIR Observation', 'Transforms Body Weight to a FHIR Observation and performs weight unit conversion.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION', 'f1da6937e2fe47a4b0f38bbff7818ee1');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('83a84beeeadc4c8ab78f8c8b9269883d', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('83a84beeeadc4c8ab78f8c8b9269883d', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('83a84beeeadc4c8ab78f8c8b9269883d', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('cd274a356f1c495a94edf935572fcac3', 0, '83a84beeeadc4c8ab78f8c8b9269883d',
'output.setCode(codeUtils.getRuleCodeableConcept());
if (output.getCode().isEmpty())
{
  output.getCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
}
output.setCategory(null);
output.addCategory().addCoding().setSystem(''http://hl7.org/fhir/observationcategory'').setCode(''vitalsigns'').setDisplay(''Vital Signs'');
var weight = input.getIntegerValue(args[''dataElement'']);
var weightUnit = vitalSignUtils.getWeightUnit(args[''weightUnit'']);
output.addChild(''valueQuantity'').setValue(args[''round''] ? Math.round(weight) : weight).setUnit(weightUnit.getUcumCode()).setSystem(''http://unitsofmeasure.org'');
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('cd274a356f1c495a94edf935572fcac3', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('cd274a356f1c495a94edf935572fcac3', 'R4');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('50a60c9bd7f24ceabbc7b633d276a2f7', 0, 'TRANSFORM_FHIR_OB_BIRTH_WEIGHT', 'Transforms FHIR Observation Birth Weight', 'Transforms FHIR Observation Birth Weight to a data element and performs weight unit conversion.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_OBSERVATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('50a60c9bd7f24ceabbc7b633d276a2f7', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('50a60c9bd7f24ceabbc7b633d276a2f7', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('50a60c9bd7f24ceabbc7b633d276a2f7', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('600187f8eba545999061b6fe8bc1c518', 0, '50a60c9bd7f24ceabbc7b633d276a2f7',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element on which the birth weight must be set.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ead06d428e4047f58a8974fbcf237b2b', 0, '50a60c9bd7f24ceabbc7b633d276a2f7',
'weightUnit', 'WEIGHT_UNIT', TRUE, 'KILO_GRAM', 'The resulting weight unit in which the value will be set on the data element.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('5e272692021444fea16c0c79f2f61917', 0, '50a60c9bd7f24ceabbc7b633d276a2f7',
'round', 'BOOLEAN', TRUE, 'true', 'Specifies if the resulting value should be rounded.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('05485e11fb1f4fc18518f9322dfb7dbb', 0, '50a60c9bd7f24ceabbc7b633d276a2f7',
'output.setValue(args[''dataElement''], vitalSignUtils.getWeight(input.value, args[''weightUnit''], args[''round'']), null, context.getFhirRequest().getLastUpdated())', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('05485e11fb1f4fc18518f9322dfb7dbb', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('05485e11fb1f4fc18518f9322dfb7dbb', 'R4');

INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('518b52d22d1e417186ce1e40ec269a1a', 0, '50a60c9bd7f24ceabbc7b633d276a2f7', 'CP: Birth Weight', 'CP_BIRTH_WEIGHT');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('75c2d252e7c24321b20bba7d5f89af80', '518b52d22d1e417186ce1e40ec269a1a', 'ead06d428e4047f58a8974fbcf237b2b', 'GRAM');

-- Tracker Program Child Programme, Birth: Weight from Body Weight
INSERT INTO fhir_executable_script (id, version, script_id, name, code, base_executable_script_id)
VALUES ('2918efbfb8f948f9921a405996bd6831', 0, '83a84beeeadc4c8ab78f8c8b9269883d', 'CP: Export Birth Weight from Body Weight', 'CP_EXP_BIRTH_WEIGHT_BODY_WEIGHT', '4a326b8949614b6980218d5285c2b0a7');
UPDATE fhir_rule
SET transform_exp_script_id = '2918efbfb8f948f9921a405996bd6831',
    exp_enabled= false
WHERE id = '097d9ee0bdb344aeb9613b4584bad1db';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT 'a11ed48e66e34988a64d7e0a69486155', 0, id, 'CODE:DE_2005736', 'dataElement', true
FROM fhir_rule
WHERE id = '097d9ee0bdb344aeb9613b4584bad1db';
DELETE
FROM fhir_executable_script_argument
WHERE id = '70684c93b22a4fff86bb604683c27fdd';

-- Tracker Program Child Programme, Baby Postnatal: Infant Weight
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('0104ad19ba8248dcbbd138dd5f0d8a2c', 0, 'f1da6937e2fe47a4b0f38bbff7818ee1', 'CP: Infant Weight', 'CP_BODY_WEIGHT');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('794c69f686b34c30b4e1fd162c6ab825', '0104ad19ba8248dcbbd138dd5f0d8a2c', '0767919959ae45309411ac5814102372', 'CODE:DE_2006099');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('5b729da43f0b49ba9059cc7fc74841d8', '0104ad19ba8248dcbbd138dd5f0d8a2c', '3d15bf81343c45bc9c281e87a8da6fa5', 'GRAM');

-- Tracker Program Child Programme, Baby Postnatal: Body Weight
INSERT INTO fhir_executable_script (id, version, script_id, name, code, base_executable_script_id)
VALUES ('06005c16cbc84a50abb07b16140c109c', 0, '83a84beeeadc4c8ab78f8c8b9269883d', 'CP: Export Infant Weight', 'CP_EXP_INFANT_WEIGHT', '0104ad19ba8248dcbbd138dd5f0d8a2c');
UPDATE fhir_rule
SET transform_exp_script_id = '06005c16cbc84a50abb07b16140c109c'
WHERE id = 'a6636c83f23648cdbb2b592147db9a34';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '9589bff180b94f9dbafb8e679a219bf1', 0, id, 'CODE:DE_2006099', 'dataElement', true
FROM fhir_rule
WHERE id = 'a6636c83f23648cdbb2b592147db9a34';
DELETE
FROM fhir_executable_script_argument
WHERE id = '794c69f686b34c30b4e1fd162c6ab825';

INSERT INTO fhir_executable_script (id, version, script_id, name, code, base_executable_script_id)
VALUES ('f8fb030926c6450d9e809e2e765ccd00', 0, '83a84beeeadc4c8ab78f8c8b9269883d', 'CP: Export Birth Weight', 'CP_EXP_BIRTH_WEIGHT', '518b52d22d1e417186ce1e40ec269a1a');
UPDATE fhir_rule
SET transform_exp_script_id = 'f8fb030926c6450d9e809e2e765ccd00'
WHERE id = 'ff043b6e40c94fa79721ba2239b38360';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '87388c6e449e466f8ef8c2b44557a10f', 0, id, 'CODE:DE_2005736', 'dataElement', true
FROM fhir_rule
WHERE id = 'ff043b6e40c94fa79721ba2239b38360';
DELETE
FROM fhir_executable_script_argument
WHERE id = '08d3eedfb73d44879fa4f2aeef3be35f';

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('cafbd01232004756a849e505aae721f7', 0, 'TRANSFORM_FHIR_OB_BODY_HEIGHT', 'Transforms FHIR Observation Body Height', 'Transforms FHIR Observation Body Height to a data element and performs height unit conversion.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_OBSERVATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('cafbd01232004756a849e505aae721f7', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('cafbd01232004756a849e505aae721f7', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('cafbd01232004756a849e505aae721f7', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('3918ef5c45c34f9c94c4e6a1fd99d4bf', 0, 'cafbd01232004756a849e505aae721f7',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element on which the body height must be set.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('60c1f864b1c3459aae0b8efeb74dd7fc', 0, 'cafbd01232004756a849e505aae721f7',
'override', 'BOOLEAN', TRUE, 'true', 'Specifies if an existing value should be overridden.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ab81fa50e9db4cebb2830364c89edf90', 0, 'cafbd01232004756a849e505aae721f7',
'heightUnit', 'HEIGHT_UNIT', TRUE, 'CENTI_METER', 'The resulting height unit in which the value will be set on the data element.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('beef9a9cb6ae48efbfda25ef277fc29e', 0, 'cafbd01232004756a849e505aae721f7',
'round', 'BOOLEAN', TRUE, 'true', 'Specifies if the resulting value should be rounded.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('0aac359d6d1b4b4fab626d79290e01a7', 0, 'cafbd01232004756a849e505aae721f7',
'output.setValue(args[''dataElement''], vitalSignUtils.getHeight(input.value, args[''heightUnit''], args[''round'']), null, args[''override''], context.getFhirRequest().getLastUpdated())', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('0aac359d6d1b4b4fab626d79290e01a7', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('0aac359d6d1b4b4fab626d79290e01a7', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('158d4ec6d9cf43819d2c4ddb9691f8f2', 0, 'cafbd01232004756a849e505aae721f7', 'Transforms FHIR Observation Body Height', 'TRANSFORM_FHIR_OB_BODY_HEIGHT');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('8a700412354c4c59aef2e996d52c36c3', 0, 'TRANSFORM_BODY_HEIGHT_FHIR_OB', 'Transforms Body Height FHIR Observation', 'Transforms Body Height to a FHIR Observation and performs height unit conversion.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION', 'cafbd01232004756a849e505aae721f7');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8a700412354c4c59aef2e996d52c36c3', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8a700412354c4c59aef2e996d52c36c3', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8a700412354c4c59aef2e996d52c36c3', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('b10bf5a234a24769a55ec7cffc895c93', 0, '8a700412354c4c59aef2e996d52c36c3',
'output.setCode(codeUtils.getRuleCodeableConcept());
if (output.getCode().isEmpty())
{
  output.getCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
}
output.setCategory(null);
output.addCategory().addCoding().setSystem(''http://hl7.org/fhir/observationcategory'').setCode(''vitalsigns'').setDisplay(''Vital Signs'');
var height = input.getIntegerValue(args[''dataElement'']);
var heightUnit = vitalSignUtils.getHeightUnit(args[''heightUnit'']);
output.addChild(''valueQuantity'').setValue(args[''round''] ? Math.round(height) : height).setCode(heightUnit.getUcumCode()).setSystem(''http://unitsofmeasure.org'').setUnit(heightUnit.getUnit());
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b10bf5a234a24769a55ec7cffc895c93', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b10bf5a234a24769a55ec7cffc895c93', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('e859a64f349d4e67bba0f2460e61fb6e', 0, '8a700412354c4c59aef2e996d52c36c3', 'Transforms Body Height FHIR Observation', 'TRANSFORM_BODY_HEIGHT_FHIR_OB');

-- Script that performs the lookup of TEI FHIR Resource from FHIR Immunization
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('d4e2822a442246a3badccf5604c6e11f', 0, 'Immunization TEI Lookup', 'IMMUNIZATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Immunization.', 'EVALUATE', 'FHIR_RESOURCE', 'FHIR_IMMUNIZATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d4e2822a442246a3badccf5604c6e11f', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d4e2822a442246a3badccf5604c6e11f', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('85b3c4606c2a4f50af46ff09bf2e69df', 0, 'd4e2822a442246a3badccf5604c6e11f', 'referenceUtils.getResource(input.patient, ''PATIENT'')', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('85b3c4606c2a4f50af46ff09bf2e69df', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('85b3c4606c2a4f50af46ff09bf2e69df', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a08caa8a1cc94f51b6b8814af781a442', 0, 'd4e2822a442246a3badccf5604c6e11f',
'Immunization TEI Lookup', 'IMMUNIZATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Immunization.');

-- Script that extracts GEO location from Immunization
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('a5079830f04c4575af5d1d6fa0bf844b', 0, 'GEO Location from Immunization', 'EXTRACT_FHIR_IMMUNIZATION_GEO_LOCATION',
'Extracts the GEO location form FHIR Immunization.',
'EVALUATE', 'LOCATION', 'FHIR_IMMUNIZATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a5079830f04c4575af5d1d6fa0bf844b', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a5079830f04c4575af5d1d6fa0bf844b', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('6427e6de84264ab1b66aedd5b0e5f410', 0, 'a5079830f04c4575af5d1d6fa0bf844b',
'var location = null;
var locationResource = referenceUtils.getResource(input.getLocation(), ''LOCATION'');
if ((locationResource != null) && locationResource.hasPosition())
{
  var position = locationResource.getPosition();
  location = geoUtils.create(position.getLongitude(), position.getLatitude());
}
location', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('6427e6de84264ab1b66aedd5b0e5f410', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('6427e6de84264ab1b66aedd5b0e5f410', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('298b4a7294ce4f4c83adc8d73436a402', 0, 'a5079830f04c4575af5d1d6fa0bf844b',  'GEO Location from Immunization', 'EXTRACT_FHIR_IMMUNIZATION_GEO_LOCATION',
'Extracts the GEO location form FHIR Immunization.');

-- Script that gets the exact date from FHIR Immunization
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('15c2a8b8b8f0443aaddacfb87a1a4378', 0, 'Immunization Date Lookup', 'IMMUNIZATION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Immunization.', 'EVALUATE', 'DATE_TIME', 'FHIR_IMMUNIZATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('15c2a8b8b8f0443aaddacfb87a1a4378', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('15c2a8b8b8f0443aaddacfb87a1a4378', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('71056dc16bd3491d908cc1494090ed65', 0, '15c2a8b8b8f0443aaddacfb87a1a4378', 'dateTimeUtils.getPreciseDate(input.hasDateElement() ? input.getDateElement() : null)', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('71056dc16bd3491d908cc1494090ed65', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('c0e2c559ff8843768063031f971072dc', 0, '15c2a8b8b8f0443aaddacfb87a1a4378',
'Immunization Date Lookup', 'IMMUNIZATION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Immunization.');

INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('f3d6c09761d748e0894e3ab30cad1299', 0, '15c2a8b8b8f0443aaddacfb87a1a4378', 'dateTimeUtils.getPreciseDate(input.hasOccurrence() ? input.getOccurrence() : null)', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version(script_source_id, fhir_version) VALUES ('f3d6c09761d748e0894e3ab30cad1299', 'R4');

-- Script that extracts Organisation Unit Reference from Tracked Entity Instance
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('0e780e509a7e4d4aa9fd1b8607d17fbb', 0, 'Org Unit Reference Code from Patient Organization', 'EXTRACT_TEI_DHIS_ORG_UNIT_ID',
'Extracts the organization unit ID reference from the tracked entity instance.', 'EVALUATE', 'ORG_UNIT_REF', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('0e780e509a7e4d4aa9fd1b8607d17fbb', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('0e780e509a7e4d4aa9fd1b8607d17fbb', 'TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('0cd71988e1164c11bed498ff608dbfb6', 0, '0e780e509a7e4d4aa9fd1b8607d17fbb',
'context.createReference(trackedEntityInstance.organizationUnitId, ''ID'')', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('0cd71988e1164c11bed498ff608dbfb6', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('0cd71988e1164c11bed498ff608dbfb6', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a52945b594b948d49c49f67b43d9dfbc', 0, '0e780e509a7e4d4aa9fd1b8607d17fbb', 'Org Unit Reference Code from Patient Organization', 'EXTRACT_TEI_DHIS_ORG_UNIT_ID',
'Extracts the organization unit ID reference from the tracked entity instance.');

-- FHIR resource mapping for FHIR Immunization
INSERT INTO fhir_resource_mapping (id,version,fhir_resource_type,tracked_entity_fhir_resource_type,imp_tei_lookup_script_id,imp_enrollment_org_lookup_script_id,imp_event_org_lookup_script_id,
imp_enrollment_date_lookup_script_id,imp_event_date_lookup_script_id,imp_enrollment_geo_lookup_script_id,imp_event_geo_lookup_script_id,imp_effective_date_lookup_script_id)
VALUES ('44a6c99cc83c4061acd239e4101de147', 0, 'IMMUNIZATION', 'PATIENT', 'a08caa8a1cc94f51b6b8814af781a442',
'a52945b594b948d49c49f67b43d9dfbc', 'a52945b594b948d49c49f67b43d9dfbc',
'c0e2c559ff8843768063031f971072dc', 'c0e2c559ff8843768063031f971072dc',
'298b4a7294ce4f4c83adc8d73436a402', '298b4a7294ce4f4c83adc8d73436a402',
'c0e2c559ff8843768063031f971072dc');

INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('2601edcbf7bc4710ab640f4edd9a2378', 0, 'CVX (Vaccine Administered)', 'SYSTEM_CVX', 'http://hl7.org/fhir/sid/cvx',
'Available at http://www2a.cdc.gov/vaccines/iis/iisstandards/vaccines.asp?rpt=cvx. Developed by The CDC''s National Center of Immunization and Respiratory Diseases (NCIRD).');

INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('7090561ef45b411e99c065fa1d145018', 0, 'Vaccine', 'VACCINE', 'Available vaccines.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f2f15a436c574d57a32dd12b468cef7e', 0, '7090561ef45b411e99c065fa1d145018', 'OPV', 'VACCINE_02', 'trivalent poliovirus vaccine, live, oral');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code, display_name, system_code_value) VALUES ('bca3b4584bb64fafa7e8d736adcafe82', 0, 'f2f15a436c574d57a32dd12b468cef7e', '2601edcbf7bc4710ab640f4edd9a2378', '02', 'OPV',
'http://hl7.org/fhir/sid/cvx|02');

-- Code set with all OPV vaccines
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('bf62319cd93c444da47cb91133b3f99a', 0, '7090561ef45b411e99c065fa1d145018', 'All OPV', 'ALL_OPV', 'All OPV vaccines.');
INSERT INTO fhir_code_set_value(id, code_set_id, code_id, enabled)
  SELECT uuid(), 'bf62319cd93c444da47cb91133b3f99a', id, true FROM fhir_code WHERE code IN ('VACCINE_02', 'VACCINE_179', 'VACCINE_178', 'VACCINE_182');

-- Tracker Program Child Programme
INSERT INTO fhir_tracker_program (id,version,name,program_ref,tracked_entity_rule_id,enabled,creation_enabled,creation_applicable_script_id,creation_script_id,enrollment_date_is_incident,tracked_entity_fhir_resource_type, exp_enabled, fhir_create_enabled,
fhir_update_enabled, fhir_delete_enabled)
VALUES ('45e61665754d4861891ea2064fc0ae7d', 0, 'Child Programme', 'NAME:Child Programme', '5f9ebdc9852e4c8387ca795946aabc35', TRUE, TRUE,
'dfb7f13cae214cfb88156f1416ca8388', '6836cbd193ba4ab8a4cb69348ad9d099', TRUE, 'PATIENT', TRUE, TRUE, TRUE, TRUE);
-- Tracker Program Child Programme, Stage Birth
INSERT INTO fhir_tracker_program_stage (id,version,name,program_stage_ref,program_id,enabled,creation_enabled,event_date_is_incident, exp_enabled, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled)
VALUES ('4c074c85be494b9d89739e16b9615dad', 0, 'Birth', 'NAME:Birth', '45e61665754d4861891ea2064fc0ae7d', TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE);
-- Tracker Program Child Programme, Stage Baby Postnatal
INSERT INTO fhir_tracker_program_stage (id,version,name,program_stage_ref,program_id,enabled,creation_enabled,event_date_is_incident, exp_enabled, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled)
VALUES ('526b4e01774747efa25df32ccd739e87', 0, 'Baby Postnatal', 'NAME:Baby Postnatal', '45e61665754d4861891ea2064fc0ae7d', TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE);

-- Rule Tracker Program Child Programme, Birth: Weight from Body Weight
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_code_set_id, transform_imp_script_id, exp_enabled, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, transform_exp_script_id)
VALUES ('097d9ee0bdb344aeb9613b4584bad1db', 0, 'Child Programme: Birth Weight from Body Weight', NULL, TRUE, 0, 'OBSERVATION', 'PROGRAM_STAGE_EVENT', 'd37dfecbce884fa49a7844ffe874c140', '4a326b8949614b6980218d5285c2b0a7', TRUE, TRUE, TRUE, TRUE, '2918efbfb8f948f9921a405996bd6831');
INSERT INTO fhir_program_stage_rule (id, program_stage_id, after_period_day_type, after_period_days,enrollment_creation_enabled,event_creation_enabled)
VALUES ('097d9ee0bdb344aeb9613b4584bad1db','4c074c85be494b9d89739e16b9615dad', 'ORIG_DUE_DATE', 1, TRUE, TRUE);

-- Rule Tracker Program Child Programme, Baby Postnatal: Infant Weight
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_code_set_id, transform_imp_script_id, exp_enabled, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, transform_exp_script_id)
VALUES ('a6636c83f23648cdbb2b592147db9a34', 0, 'Child Programme: Infant Weight', NULL, TRUE, 10, 'OBSERVATION', 'PROGRAM_STAGE_EVENT', 'd37dfecbce884fa49a7844ffe874c140', '0104ad19ba8248dcbbd138dd5f0d8a2c', TRUE, TRUE, TRUE, TRUE, '06005c16cbc84a50abb07b16140c109c');
INSERT INTO fhir_program_stage_rule (id, program_stage_id, before_period_day_type, after_period_day_type, after_period_days,enrollment_creation_enabled,event_creation_enabled)
VALUES ('a6636c83f23648cdbb2b592147db9a34','526b4e01774747efa25df32ccd739e87', 'ORIG_DUE_DATE', 'ORIG_DUE_DATE', 1,TRUE,TRUE);

INSERT INTO fhir_script (id, version, created_at, last_updated_at, last_updated_by, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('f18acd12bc854f79935d353904eadc0b', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'TRANSFORM_FHIR_IMMUNIZATION_OS', 'Transforms FHIR Immunization to option set data element', 'Transforms FHIR Immunization to an option set data element.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_IMMUNIZATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f18acd12bc854f79935d353904eadc0b', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f18acd12bc854f79935d353904eadc0b', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f18acd12bc854f79935d353904eadc0b', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('44134ba8d77f4c4d90c6b434ffbe7958', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f18acd12bc854f79935d353904eadc0b',
'dataElement', 'DATA_ELEMENT_REF', TRUE, FALSE, NULL, 'Data element with given vaccine on which option set value must be set.');
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('404ae6f6618749148f4b80a72764c1d8', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f18acd12bc854f79935d353904eadc0b',
'optionValuePattern', 'PATTERN', FALSE, FALSE, NULL, 'Regular expression pattern to extract subsequent integer option value from option code. If the pattern is not specified the whole code will be used as an integer value.');
INSERT INTO fhir_script_source (id, version, created_at, last_updated_at, last_updated_by, script_id, source_text, source_type)
VALUES ('081c4642bb8344abb90faa206ad347aa', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f18acd12bc854f79935d353904eadc0b',
'output.setIntegerOptionValue(args[''dataElement''], immunizationUtils.getMaxDoseSequence(input), 1, false, args[''optionValuePattern''], (input.hasPrimarySource()?!input.getPrimarySource():null))', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('081c4642bb8344abb90faa206ad347aa', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('081c4642bb8344abb90faa206ad347aa', 'R4');

INSERT INTO fhir_script (id, version, created_at, last_updated_at, last_updated_by, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('f18acd12bc854f79935d353904eadc0c', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'TEST', 'Test script', 'Test script.',
        'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_IMMUNIZATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f18acd12bc854f79935d353904eadc0c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f18acd12bc854f79935d353904eadc0c', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f18acd12bc854f79935d353904eadc0c', 'OUTPUT');

INSERT INTO fhir_executable_script (id, version, created_at, last_updated_at, last_updated_by, script_id, name, code, description)
VALUES ('1a2950cf08424dd39453284fb08789d3', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f18acd12bc854f79935d353904eadc0b', 'CP: OPV Dose', 'CP_OPV_DOSE', 'Transforms FHIR Immunization for OPV vaccines.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value, enabled)
VALUES ('4a8ba21510e946f2921fda3973836119', '1a2950cf08424dd39453284fb08789d3', '44134ba8d77f4c4d90c6b434ffbe7958', 'CODE:DE_2006104', TRUE);

-- Rule Tracker Program Child Programme, Birth: OPV Dose
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_imp_script_id, applicable_code_set_id, transform_imp_script_id)
VALUES ('91ae6f5fdb07439197cd407a77794a1b', 0, 'Child Programme: OPV', NULL, TRUE, 0, 'IMMUNIZATION', 'PROGRAM_STAGE_EVENT', NULL, 'bf62319cd93c444da47cb91133b3f99a', '1a2950cf08424dd39453284fb08789d3');
INSERT INTO fhir_program_stage_rule (id, program_stage_id,enrollment_creation_enabled,event_creation_enabled)
VALUES ('91ae6f5fdb07439197cd407a77794a1b','4c074c85be494b9d89739e16b9615dad',TRUE,TRUE);

-- Script that sets the FHIR observation to the status of the DHIS event
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('1e06ef0524664ed9b32e7f9dfecf4d45', 0, 'TRANSFORM_EVENT_FHIR_EN', 'Transforms DHIS event to FHIR Encounter', 'Transforms DHIS event to FHIR Encounter.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_ENCOUNTER');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1e06ef0524664ed9b32e7f9dfecf4d45', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1e06ef0524664ed9b32e7f9dfecf4d45', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1e06ef0524664ed9b32e7f9dfecf4d45', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('cfec6b38477749fd944dddab80421bc2', 0, '1e06ef0524664ed9b32e7f9dfecf4d45',
'output.setType(null);
output.addType().setText(program.getName() + '': '' + programStage.getName());
context.setAttribute(''groupencounterevent'' + input.getId(), output);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('cfec6b38477749fd944dddab80421bc2', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('cfec6b38477749fd944dddab80421bc2', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('5eab76a90ff443b0a7d05a6e726ca80e', 0, '1e06ef0524664ed9b32e7f9dfecf4d45',
        'Transforms DHIS event to FHIR Encounter', 'TRANSFORM_EVENT_FHIR_EN',
        'Transforms DHIS event to FHIR Encounter.');

INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('ac0af564c3ee49799ac3d36b04ddb73d', 0, '1197b27e395643dda75cbfc6808dc49d', 'All Body Height Observations', 'ALL_OB_BODY_HEIGHT', 'All Body Height Observations.');
INSERT INTO fhir_code_set_value(code_set_id, code_id)
  SELECT 'ac0af564c3ee49799ac3d36b04ddb73d', id FROM fhir_code WHERE code IN
  ('LOINC_83022', 'LOINC_83014', 'LOINC_31385', 'LOINC_31377');
UPDATE fhir_code_set_value SET preferred_export=true WHERE code_id='d308a6acad84453d9fb6e04f6a468469';

-- Encounter Child Programme, Birth
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, transform_exp_script_id, fhir_create_enabled, fhir_update_enabled, exp_enabled, grouping, simple_fhir_id)
VALUES ('9d342f13aec146299d654f03fd0e848c', 0, 'Child Programme Birth Encounter', NULL, TRUE, 1000, 'ENCOUNTER', 'PROGRAM_STAGE_EVENT', TRUE, '5eab76a90ff443b0a7d05a6e726ca80e', TRUE, TRUE, TRUE, TRUE, TRUE);
INSERT INTO fhir_program_stage_rule (id, program_stage_id)
VALUES ('9d342f13aec146299d654f03fd0e848c', '4c074c85be494b9d89739e16b9615dad');

-- Encounter Child Programme, Baby Postnatal
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, transform_exp_script_id, fhir_create_enabled, fhir_update_enabled, exp_enabled, grouping, simple_fhir_id)
VALUES ('1f2da6ec41b04b6499d9e98fca864b0f', 0, 'Child Programme Baby Postnatal Encounter', NULL, TRUE, 1000, 'ENCOUNTER', 'PROGRAM_STAGE_EVENT', FALSE, '5eab76a90ff443b0a7d05a6e726ca80e', TRUE, TRUE, TRUE, TRUE, TRUE);
INSERT INTO fhir_program_stage_rule (id, program_stage_id)
VALUES ('1f2da6ec41b04b6499d9e98fca864b0f', '526b4e01774747efa25df32ccd739e87');

INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('20e1ac3ff49c4fa893e2b5e18c5f9665', 0, 'Vital Signs New Program Stage', 'VITAL_SIGN_NEW_PS', 'Decides about creation of a new program stage for vital signs.', 'EVALUATE', 'EVENT_DECISION_TYPE', NULL, 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('20e1ac3ff49c4fa893e2b5e18c5f9665', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('20e1ac3ff49c4fa893e2b5e18c5f9665', 'PROGRAM_STAGE_EVENTS');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('20e1ac3ff49c4fa893e2b5e18c5f9665', 'DATE_TIME');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('09db6430cfb24861befbaf7408e35c2e', 0, '20e1ac3ff49c4fa893e2b5e18c5f9665',
        'programStageUtils.containsEventDay(programStageEvents, dateTime) ? ''CONTINUE'' : ''NEW_EVENT''', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('09db6430cfb24861befbaf7408e35c2e', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('09db6430cfb24861befbaf7408e35c2e', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('5f1b7d9848ad4df4bdb223e7de8f0fc1', 0, '20e1ac3ff49c4fa893e2b5e18c5f9665',
'Vital Signs New Program Stage', 'VITAL_SIGN_NEW_PS', 'Decides about creation of a new program stage for vital signs.');

INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('07febf71a102408e8c5926d2a6536a39', 0, 'f1da6937e2fe47a4b0f38bbff7818ee1', 'Transforms FHIR Observation Body Weight', 'TRANSFORM_FHIR_OB_BODY_WEIGHT');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('85653f4cf09940728be8e2713d0c865a', '07febf71a102408e8c5926d2a6536a39', '5e272692021444fea16c0c79f2f61917', 'false');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('ddc7c612aabb4ec4bd636d5220cf666f', 0, '83a84beeeadc4c8ab78f8c8b9269883d', 'Transforms Body Weight FHIR Observation', 'TRANSFORM_BODY_WEIGHT_FHIR_OB');

INSERT INTO fhir_tracker_program (id,version,name,program_ref,tracked_entity_rule_id,enabled,creation_enabled,enrollment_date_is_incident,fhir_create_enabled,fhir_update_enabled,fhir_delete_enabled,tracked_entity_fhir_resource_type)
VALUES ('fe122800620740719b1f161e2844c72f', 0, 'Vital Signs', 'NAME:Vital Signs', '5f9ebdc9852e4c8387ca795946aabc35', FALSE, TRUE, TRUE, TRUE, TRUE, TRUE,'PATIENT');
INSERT INTO fhir_tracker_program_stage (id,version,name,program_stage_ref,program_id,enabled,creation_enabled,event_date_is_incident,before_script_id,fhir_create_enabled,fhir_update_enabled,fhir_delete_enabled)
VALUES ('eca2b04a54e84b7595fa8035f7477ed8', 0, 'Daily Vital Signs', 'NAME:Daily Vital Signs', 'fe122800620740719b1f161e2844c72f', TRUE, TRUE, FALSE, '5f1b7d9848ad4df4bdb223e7de8f0fc1', TRUE, TRUE, TRUE);

INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, transform_imp_script_id, transform_exp_script_id, fhir_create_enabled, fhir_update_enabled, exp_enabled, applicable_code_set_id)
VALUES ('75b9786f87624c54a5a5bdae358a62e5', 0, 'Daily Vital Signs: Weight', NULL, TRUE, 1, 'OBSERVATION', 'PROGRAM_STAGE_EVENT', TRUE, '07febf71a102408e8c5926d2a6536a39', 'ddc7c612aabb4ec4bd636d5220cf666f', TRUE, TRUE, TRUE, 'd37dfecbce884fa49a7844ffe874c140');
INSERT INTO fhir_program_stage_rule (id, program_stage_id, event_creation_enabled, enrollment_creation_enabled)
VALUES ('75b9786f87624c54a5a5bdae358a62e5', 'eca2b04a54e84b7595fa8035f7477ed8', true, true);
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
VALUES('7edd49b3e32443cf858be909d97bccf1', 0, '75b9786f87624c54a5a5bdae358a62e5', 'CODE:VS_982701', 'dataElement', true);

INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, transform_imp_script_id, transform_exp_script_id, fhir_create_enabled, fhir_update_enabled, exp_enabled,
                       applicable_code_set_id)
VALUES ('0035e8b199e04cedacf7726ab26d9483', 0, 'Daily Vital Signs: Height', NULL, TRUE, 1, 'OBSERVATION', 'PROGRAM_STAGE_EVENT', TRUE, '158d4ec6d9cf43819d2c4ddb9691f8f2', 'e859a64f349d4e67bba0f2460e61fb6e', TRUE, TRUE, TRUE, 'ac0af564c3ee49799ac3d36b04ddb73d');
INSERT INTO fhir_program_stage_rule (id, program_stage_id, event_creation_enabled, enrollment_creation_enabled)
VALUES ('0035e8b199e04cedacf7726ab26d9483', 'eca2b04a54e84b7595fa8035f7477ed8', true, true);
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
VALUES('d2751686cc9f4bc69f44068fe6d806f3', 0, '0035e8b199e04cedacf7726ab26d9483', 'CODE:VS_982702', 'dataElement', true);
UPDATE fhir_rule_dhis_data_ref SET rule_id='0035e8b199e04cedacf7726ab26d9483'
WHERE id='d2751686cc9f4bc69f44068fe6d806f3';

-- Script that sets the FHIR observation to absent
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('ba1f2dadc0084ded8b02108f94fad74c', 0, 'TRANSFORM_ABSENT_FHIR_OB', 'Transforms absence of data element to FHIR Observation', 'Transforms absence of data element to FHIR Observation.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ba1f2dadc0084ded8b02108f94fad74c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ba1f2dadc0084ded8b02108f94fad74c', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ba1f2dadc0084ded8b02108f94fad74c', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('efeb5e9857604d12a0c420efb1d71174', 0, 'ba1f2dadc0084ded8b02108f94fad74c',
'output.setStatus(observationUtils.getObservationStatus(''enteredinerror'')); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('efeb5e9857604d12a0c420efb1d71174', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('efeb5e9857604d12a0c420efb1d71174', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('9d59115605de4e128986dfcaaa1f0f25', 0, 'ba1f2dadc0084ded8b02108f94fad74c',
        'Transforms absence of data element to FHIR Observation', 'TRANSFORM_ABSENT_FHIR_OB',
        'Transforms absence of data element to FHIR Observation.');

-- Script that sets the FHIR observation to the status of the DHIS event
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('6a644b7878784512adada710a5c901d4', 0, 'TRANSFORM_STATUS_FHIR_OB', 'Transforms DHIS event status to FHIR Observation', 'Transforms DHIS event status to FHIR Observation.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6a644b7878784512adada710a5c901d4', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6a644b7878784512adada710a5c901d4', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6a644b7878784512adada710a5c901d4', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('b3d2ce26ec5f480dabc6910bedc7d38c', 0, '6a644b7878784512adada710a5c901d4',
'output.setStatus(observationUtils.getObservationStatus(input.getStatus() == ''COMPLETED'' ? ''final'' : ''preliminary'')); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b3d2ce26ec5f480dabc6910bedc7d38c', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b3d2ce26ec5f480dabc6910bedc7d38c', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a78e3852e18b44bca2c69cae29974439', 0, '6a644b7878784512adada710a5c901d4',
        'Transforms DHIS event status to FHIR Observation', 'TRANSFORM_STATUS_FHIR_OB',
        'Transforms DHIS event status to FHIR Observation.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('094a3f6b6f364a4983085a05b0acc4ce', 0, 'Transforms DHIS event date to FHIR Resource', 'TRANSFORM_DHIS_EVENT_DATE_FHIR_PATIENT',
        'Transforms DHIS event date to FHIR Resource.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('094a3f6b6f364a4983085a05b0acc4ce', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('094a3f6b6f364a4983085a05b0acc4ce', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('4a0b6fdec0d64ad289da992f4a47a115', 0, '094a3f6b6f364a4983085a05b0acc4ce',
'var updated = false;
if (typeof output.dateElement !== ''undefined'')
{
  output.setDateElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.effective !== ''undefined'')
{
  output.setEffective(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.period !== ''undefined'')
{
  output.setPeriod(null);
  output.getPeriod().setStartElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
updated', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4a0b6fdec0d64ad289da992f4a47a115', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4a0b6fdec0d64ad289da992f4a47a115', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('deb4fd13d5b241df9f300fb73b063c8b', 0, '094a3f6b6f364a4983085a05b0acc4ce',
        'Transforms DHIS event date to FHIR Resource', 'TRANSFORM_DHIS_EVENT_DATE_FHIR_PATIENT',
        'Transforms DHIS event date to FHIR Resource.');

-- Script that sets the collected FHIR encounter to the updated FHIR resource
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('45c39ea93f444db98ce534d23031db27', 0, 'TRANSFORM_GROUP_FHIR', 'Transforms the grouping FHIR resource to FHIR resource', 'Transforms the grouping FHIR resource to FHIR resource.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('45c39ea93f444db98ce534d23031db27', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('45c39ea93f444db98ce534d23031db27', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('45c39ea93f444db98ce534d23031db27', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('9c4c5b8852b84baeb07f7f3ee7392d13', 0, '45c39ea93f444db98ce534d23031db27',
'var encounter = context.getAttribute(''groupencounterevent'' + input.getId());
var updated = false;
if (output.encounter)
{
  output.setEncounter(null);
  if (encounter != null)
  {
    output.setEncounter(fhirResourceUtils.createReference(encounter));
  }
  updated = true;
}
else if (output.context)
{
  output.setContext(null);
  if (encounter != null)
  {
    output.setContext(fhirResourceUtils.createReference(encounter));
  }
  updated = true;
}
updated', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('9c4c5b8852b84baeb07f7f3ee7392d13', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('9c4c5b8852b84baeb07f7f3ee7392d13', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('2347ba1f2d5b42768934100cbdb0fcfa', 0, '45c39ea93f444db98ce534d23031db27',
        'Transforms the grouping FHIR resource to FHIR resource', 'TRANSFORM_GROUP_FHIR',
        'Transforms the grouping FHIR resource to FHIR resource.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('190ae3fa471e458abe1d3f173f7d3c75', 0, 'Transforms TEI FHIR Patient to FHIR Resource', 'TRANSFORM_TEI_FHIR_PATIENT',
        'Transforms TEI FHIR Patient to FHIR Resource.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('190ae3fa471e458abe1d3f173f7d3c75', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('190ae3fa471e458abe1d3f173f7d3c75', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('190ae3fa471e458abe1d3f173f7d3c75', 'TEI_FHIR_RESOURCE');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('17931cd6b2dc4bb9a9b0bec16139ce07', 0, '190ae3fa471e458abe1d3f173f7d3c75',
'var updated = false;
if (output.patient)
{
  output.setPatient(fhirResourceUtils.createReference(teiFhirResource));
  updated = true;
}
else if (output.subject)
{
  output.setSubject(fhirResourceUtils.createReference(teiFhirResource));
  updated = true;
}
updated', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('17931cd6b2dc4bb9a9b0bec16139ce07', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('17931cd6b2dc4bb9a9b0bec16139ce07', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('f7863a1786da42d289fd7f6c3d214f1b', 0, '190ae3fa471e458abe1d3f173f7d3c75',
        'Transforms TEI FHIR Patient to FHIR Resource', 'TRANSFORM_TEI_FHIR_PATIENT',
        'Transforms TEI FHIR Patient to FHIR Resource.');

-- Script that gets the exact date from FHIR Observation
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('49d3570129794b36a9ba4269c1572cfd', 0, 'Observation Date Lookup', 'OBSERVATION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Observation.', 'EVALUATE', 'DATE_TIME', 'FHIR_OBSERVATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('49d3570129794b36a9ba4269c1572cfd', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('49d3570129794b36a9ba4269c1572cfd', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('630cd8383506410b9b668785682d5e0c', 0, '49d3570129794b36a9ba4269c1572cfd',
'var date = null;
if (input.hasEffectiveDateTimeType())
  date = dateTimeUtils.getPreciseDate(input.getEffectiveDateTimeType());
else if (input.hasEffectivePeriod())
  date = dateTimeUtils.getPreciseDate(input.getEffectivePeriod().hasStart() ? input.getEffectivePeriod().getStartElement() : null);
else if (input.hasIssued())
  date = dateTimeUtils.getPreciseDate(input.getIssuedElement());
date', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('630cd8383506410b9b668785682d5e0c', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('630cd8383506410b9b668785682d5e0c', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a7b604369fa74fe48bf7f5e22123a980', 0, '49d3570129794b36a9ba4269c1572cfd',
'Observation Date Lookup', 'OBSERVATION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Observation.');

-- Script that extracts GEO location from Observation
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('efb7fa7adf454c6a90960bc38f08c067', 0, 'GEO Location from Observation', 'EXTRACT_FHIR_OBSERVATION_GEO_LOCATION',
'Extracts the GEO location form FHIR Observation.',
'EVALUATE', 'LOCATION', 'FHIR_OBSERVATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('efb7fa7adf454c6a90960bc38f08c067', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('efb7fa7adf454c6a90960bc38f08c067', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('2e0565a0ecde4ce2b313b0f786105385', 0, 'efb7fa7adf454c6a90960bc38f08c067',
'null', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('2e0565a0ecde4ce2b313b0f786105385', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('2e0565a0ecde4ce2b313b0f786105385', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('bb07063146b342ec83b200ea219bcf50', 0, 'efb7fa7adf454c6a90960bc38f08c067',  'GEO Location from Observation', 'EXTRACT_FHIR_OBSERVATION_GEO_LOCATION',
'Extracts the GEO location form FHIR Observation.');

-- Script that performs the lookup of TEI FHIR Resource from FHIR Observation
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('8b5ab5f1363d4ccb8e63d6ecf25b3017', 0, 'Observation TEI Lookup', 'OBSERVATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Observation.', 'EVALUATE', 'FHIR_RESOURCE', 'FHIR_OBSERVATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8b5ab5f1363d4ccb8e63d6ecf25b3017', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8b5ab5f1363d4ccb8e63d6ecf25b3017', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('960d2e6c247948a2b04eb14879e71d14', 0, '8b5ab5f1363d4ccb8e63d6ecf25b3017', 'var fhirResource = null;
if (context.getFhirRequest().isDhisFhirId())
{
  fhirResource = fhirResourceUtils.createResource(''Patient'');
  fhirResource.setId(input.getSubject().getReferenceElement());
}
else
{
  fhirResource = referenceUtils.getResource(input.subject, ''Patient'');
}
fhirResource', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('960d2e6c247948a2b04eb14879e71d14', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('960d2e6c247948a2b04eb14879e71d14', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('1b6a2f75cb4a47b18e903dfb4db07d36', 0, '8b5ab5f1363d4ccb8e63d6ecf25b3017',
'Observation TEI Lookup', 'OBSERVATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Observation.');

INSERT INTO public.fhir_resource_mapping (id, version, fhir_resource_type, last_updated_by, imp_tei_lookup_script_id, imp_enrollment_org_lookup_script_id, imp_event_org_lookup_script_id, imp_enrollment_date_lookup_script_id, imp_event_date_lookup_script_id, imp_enrollment_geo_lookup_script_id, imp_event_geo_lookup_script_id, imp_effective_date_lookup_script_id, exp_ou_transform_script_id, exp_geo_transform_script_id, exp_date_transform_script_id, exp_absent_transform_script_id, exp_status_transform_script_id, exp_delete_when_absent, exp_tei_transform_script_id, exp_group_transform_script_id, tracked_entity_fhir_resource_type) VALUES
('f1cbd84fa3db4aa7a9f2a5e547e60bed', 0, 'OBSERVATION', null, '1b6a2f75cb4a47b18e903dfb4db07d36', '25a97bb47b394ed48677db4bcaa28ccf', '25a97bb47b394ed48677db4bcaa28ccf', 'a7b604369fa74fe48bf7f5e22123a980', 'a7b604369fa74fe48bf7f5e22123a980', 'bb07063146b342ec83b200ea219bcf50', 'bb07063146b342ec83b200ea219bcf50', 'a7b604369fa74fe48bf7f5e22123a980', '416decee4604473ab6501a997d731ff0', null, 'deb4fd13d5b241df9f300fb73b063c8b', '9d59115605de4e128986dfcaaa1f0f25', 'a78e3852e18b44bca2c69cae29974439', true, 'f7863a1786da42d289fd7f6c3d214f1b', '2347ba1f2d5b42768934100cbdb0fcfa', 'PATIENT');

INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT 'a11ed48e66e34988a64d7e0a69486155', 0, id, 'CODE:DE_2005736', 'dataElement', true
FROM fhir_rule
WHERE id = '097d9ee0bdb344aeb9613b4584bad1db';
DELETE
FROM fhir_executable_script_argument
WHERE id = '70684c93b22a4fff86bb604683c27fdd';

INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '9589bff180b94f9dbafb8e679a219bf1', 0, id, 'CODE:DE_2006099', 'dataElement', true
FROM fhir_rule
WHERE id = 'a6636c83f23648cdbb2b592147db9a34';
DELETE
FROM fhir_executable_script_argument
WHERE id = '794c69f686b34c30b4e1fd162c6ab825';

-- Script that sets the FHIR encounter to absent
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('37323ef85dd24d86b82da9c43b0df27a', 0, 'TRANSFORM_ABSENT_FHIR_EN', 'Transforms absence of data element to FHIR Encounter', 'Transforms absence of data element to FHIR Encounter.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_ENCOUNTER');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('37323ef85dd24d86b82da9c43b0df27a', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('37323ef85dd24d86b82da9c43b0df27a', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('37323ef85dd24d86b82da9c43b0df27a', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('6dd56290b5134c9aba4b6c5169b16559', 0, '37323ef85dd24d86b82da9c43b0df27a',
'output.setStatus(encounterUtils.getEncounterStatus(''cancelled'')); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('6dd56290b5134c9aba4b6c5169b16559', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('c51681da3d4c4936bec991f53cc05953', 0, '37323ef85dd24d86b82da9c43b0df27a',
        'Transforms absence of data element to FHIR Encounter', 'TRANSFORM_ABSENT_FHIR_EN',
        'Transforms absence of data element to FHIR Encounter.');

-- Script that sets the FHIR encounter to the status of the DHIS event
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('ee82d0caa5d6417f9daedbe84ccf8964', 0, 'TRANSFORM_STATUS_FHIR_EN', 'Transforms DHIS event status to FHIR Encounter', 'Transforms DHIS event status to FHIR Encounter.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_ENCOUNTER');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ee82d0caa5d6417f9daedbe84ccf8964', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ee82d0caa5d6417f9daedbe84ccf8964', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ee82d0caa5d6417f9daedbe84ccf8964', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('78490c08217f40ba8663d51dbe730b6d', 0, 'ee82d0caa5d6417f9daedbe84ccf8964',
'var eventStatus = input.getStatus();
var encounterStatus = ''planned'';
if (eventStatus == ''COMPLETED'')
{
  encounterStatus = ''finished'';
}
else if (eventStatus == ''SKIPPED'')
{
  encounterStatus = ''cancelled'';
}
else if (eventStatus == ''ACTIVE'')
{
  encounterStatus = ''inprogress'';
}
output.setStatus(encounterUtils.getEncounterStatus(encounterStatus));
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('78490c08217f40ba8663d51dbe730b6d', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a726bc90815248a3942755477a948907', 0, 'ee82d0caa5d6417f9daedbe84ccf8964',
        'Transforms DHIS event status to FHIR Encounter', 'TRANSFORM_STATUS_FHIR_EN',
        'Transforms DHIS event status to FHIR Encounter.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('ed2e0cdefc1944688d846d31841d55e6', 0, 'Transforms GEO Coordinates to FHIR Resource', 'TRANSFORM_DHIS_GEO_FHIR',
        'Transforms GEO coordinates to FHIR Resource.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('ed2e0cdefc1944688d846d31841d55e6', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('ed2e0cdefc1944688d846d31841d55e6', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('dcf956e823cc49348e420fbc9a23eeb9', 0, 'ed2e0cdefc1944688d846d31841d55e6',
'if (input.coordinate && output.location && (input.getCoordinate() != null))
{
  var location = fhirResourceUtils.createResource(''Location'');
  location.setPosition(geoUtils.createPosition(input.getCoordinate()));
  if (typeof output.getLocationFirstRep !== ''undefined'')
  {
    location.setPartOf(output.getLocationFirstRep().getLocation());
    output.setLocation(null);
    output.getLocationFirstRep().setLocation(fhirResourceUtils.createReference(location));
  }
  else
  {
    location.setPartOf(output.getLocation());
    output.setLocation(fhirResourceUtils.createReference(location));
  }
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('dcf956e823cc49348e420fbc9a23eeb9', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('30ee57d1062f484785b9f2262a678151', 0, 'ed2e0cdefc1944688d846d31841d55e6',
        'Transforms GEO Coordinates to FHIR Resource', 'TRANSFORM_DHIS_GEO_FHIR',
        'Transforms GEO coordinates to FHIR Resource.');

INSERT INTO fhir_resource_mapping(id, version, fhir_resource_type,
                                  exp_absent_transform_script_id, exp_status_transform_script_id,
                                  exp_tei_transform_script_id, exp_ou_transform_script_id, exp_geo_transform_script_id,
                                  exp_date_transform_script_id)
VALUES ('248d964c4dbf4271bb5722f5aed5c9f9', 0, 'ENCOUNTER',
        'c51681da3d4c4936bec991f53cc05953', 'a726bc90815248a3942755477a948907',
        'f7863a1786da42d289fd7f6c3d214f1b', '416decee4604473ab6501a997d731ff0', '30ee57d1062f484785b9f2262a678151',
        'deb4fd13d5b241df9f300fb73b063c8b');
UPDATE fhir_resource_mapping SET exp_group_transform_script_id = '2347ba1f2d5b42768934100cbdb0fcfa' WHERE fhir_resource_type='OBSERVATION';

UPDATE fhir_script_source SET source_text=
'searchFilter.add(''name'', ''string'', ''name'');
searchFilter.addReference(''partof'', ''location'', ''organization-unit'', ''parent.id'');
searchFilter.addToken(''status'', ''active'', ''closedDate'', ''null'', null);
searchFilter.addToken(''status'', ''inactive'', ''closedDate'', ''!null'', null);
true' WHERE id = '10200cc2f0604aa8a9070d12b199ff3b' AND version = 0;

UPDATE fhir_script_source SET source_text=
'searchFilter.add(''name'', ''string'', ''name'');
searchFilter.addReference(''partof'', ''organization'', ''organization-unit'', ''parent.id'');
searchFilter.addToken(''status'', ''true'', ''closedDate'', ''null'', null);
searchFilter.addToken(''status'', ''false'', ''closedDate'', ''!null'', null);
true' WHERE id = '1f1f0ce95b9b4ce1b8824e40713314bf' AND version = 0;

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('ebf89e1eede9408ca523c9912a72d376', 0, 'SEARCH_FILTER_ENCOUNTER', 'Prepares Encounter Search Filter', 'Prepares Encounter Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ebf89e1eede9408ca523c9912a72d376', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('4ff5f488210943d0b44dd9ab7241bc39', 0, 'ebf89e1eede9408ca523c9912a72d376',
'searchFilter.addReference(''subject'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''location'', ''location'', ''organization-unit'', ''orgUnit'');
searchFilter.addToken(''status'', ''in-progress'', ''status'', ''eq'', ''ACTIVE'');
searchFilter.addToken(''status'', ''skipped'', ''status'', ''eq'', ''CANCELLED'');
searchFilter.addToken(''status'', ''finished'', ''status'', ''eq'', ''COMPLETED'');
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4ff5f488210943d0b44dd9ab7241bc39', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4ff5f488210943d0b44dd9ab7241bc39', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('bae38b4e5d1646e9b579a9a0900edb72', 0, 'ebf89e1eede9408ca523c9912a72d376', 'Prepares Encounter Search Filter', 'SEARCH_FILTER_ENCOUNTER');
UPDATE fhir_rule SET filter_script_id='bae38b4e5d1646e9b579a9a0900edb72' WHERE fhir_resource_type='ENCOUNTER' and dhis_resource_type='PROGRAM_STAGE_EVENT' and filter_script_id IS NULL;

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('45767b712bb04a20bbd79932ed0e6d83', 0, 'SEARCH_FILTER_OBSERVATION', 'Prepares Observation Search Filter', 'Prepares Observation Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('45767b712bb04a20bbd79932ed0e6d83', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('a99c0dfc00554aff90e3f313d0bb82a6', 0, '45767b712bb04a20bbd79932ed0e6d83',
'searchFilter.addReference(''patient'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''subject'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''encounter'', ''encounter'', ''program-stage-event'', ''event'');
searchFilter.addToken(''status'', ''final'', ''status'', ''eq'', ''COMPLETED'');
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('a99c0dfc00554aff90e3f313d0bb82a6', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('a99c0dfc00554aff90e3f313d0bb82a6', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('a97e64a781d14f6284f2da9a003f9d0b', 0, '45767b712bb04a20bbd79932ed0e6d83', 'Prepares Observation Search Filter', 'SEARCH_FILTER_OBSERVATION');
UPDATE fhir_rule SET filter_script_id='a97e64a781d14f6284f2da9a003f9d0b' WHERE fhir_resource_type='OBSERVATION' and dhis_resource_type='PROGRAM_STAGE_EVENT' and filter_script_id IS NULL;

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('8bf44a88fbbe4f2ab42b89d1da7751b6', 0, 'SEARCH_FILTER_IMMUNIZATION', 'Prepares Immunization Search Filter', 'Prepares Immunization Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8bf44a88fbbe4f2ab42b89d1da7751b6', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('2da4f14b3d094f12bc2c3b228d0a8f4e', 0, '8bf44a88fbbe4f2ab42b89d1da7751b6',
'searchFilter.addReference(''patient'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''encounter'', ''encounter'', ''program-stage-event'', ''event'');
searchFilter.addToken(''status'', ''final'', ''status'', ''eq'', ''COMPLETED'');
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('2da4f14b3d094f12bc2c3b228d0a8f4e', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('2da4f14b3d094f12bc2c3b228d0a8f4e', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('2e3c191f8cf14a9098764e9b0b6e4e8c', 0, '8bf44a88fbbe4f2ab42b89d1da7751b6', 'Prepares Immunization Search Filter', 'SEARCH_FILTER_IMMUNIZATION');
UPDATE fhir_rule SET filter_script_id='2e3c191f8cf14a9098764e9b0b6e4e8c' WHERE fhir_resource_type='IMMUNIZATION' and dhis_resource_type='PROGRAM_STAGE_EVENT' and filter_script_id IS NULL;

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, base_script_id)
VALUES ('6f1a456adb0945038bb69bba687f13b4', 0, 'SEARCH_FILTER_PATIENT', 'Prepares Patient Search Filter', 'Prepares Patient Search Filter.', 'SEARCH_FILTER', 'BOOLEAN', 'ea8879435e944e319441c7661fe1063e');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6f1a456adb0945038bb69bba687f13b4', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('49def204d98d4b21bd2466aeda141592', 0, '6f1a456adb0945038bb69bba687f13b4',
'searchFilter.addReference(''organization'', ''organization'', ''organization-unit'', ''ou'');
searchFilter.add(''given'', ''string'', args[''firstNameAttribute'']);
searchFilter.add(''name'', ''string'', args[''lastNameAttribute'']);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('49def204d98d4b21bd2466aeda141592', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('49def204d98d4b21bd2466aeda141592', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, base_executable_script_id)
VALUES ('a2b0c480d4b14a30abbc44f720402d5a', 0, '6f1a456adb0945038bb69bba687f13b4', 'Prepares Patient Search Filter', 'SEARCH_FILTER_PATIENT', '72451c8f7492470790b8a3e0796de19e');
UPDATE fhir_rule SET filter_script_id='a2b0c480d4b14a30abbc44f720402d5a' WHERE fhir_resource_type='PATIENT' and dhis_resource_type='TRACKED_ENTITY'
and filter_script_id IS NULL and id = '5f9ebdc9852e4c8387ca795946aabc35';

UPDATE fhir_script_source SET source_text='fhirResourceUtils.getIdentifiedResource(input.subject, ''Patient'')'
WHERE id='960d2e6c247948a2b04eb14879e71d14' AND version=0;

UPDATE fhir_script_source SET source_text='fhirResourceUtils.getIdentifiedResource(input.patient, ''Patient'')'
WHERE id='1f94dda828ec480f8c6bd8d734612414' AND version=0;

UPDATE fhir_script_source SET source_text='fhirResourceUtils.getIdentifiedResource(input.patient, ''Patient'')'
WHERE id='85b3c4606c2a4f50af46ff09bf2e69df' AND version=0;

UPDATE fhir_script_source SET source_text='fhirResourceUtils.getIdentifiedResource(input.subject, ''Patient'')'
WHERE id='67249e7b4ba7466ca770a78923fbf1c3' AND version=0;

UPDATE fhir_script_source SET source_text=
'var ok = false;
var code = null;
if (output.location)
{
  if (context.getDhisRequest().isDhisFhirId())
  {
    var ref = context.getDhisFhirResourceReference(organizationUnit, ''Location'');
    if (ref != null)
    {
      if (typeof output.addLocation === ''undefined'')
      {
        output.setLocation(ref);
      }
      else
      {
        output.addLocation().setLocation(ref);
      }
      ok = true;
    }
  }
  else if (!output.getLocation().isEmpty() && !args[''overrideExisting''])
  {
    ok = true;
  }
  else if ((organizationUnit == null) || (organizationUnit.getCode() == null) || organizationUnit.getCode().isEmpty())
  {
    output.setLocation(null);
    ok = true;
  }
  else
  {
    code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''LOCATION'');
    if ((code == null) && args[''useLocationIdentifierCode''])
    {
      code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''LOCATION'');
    }
  }
  if (code != null)
  {
    var resource = fhirClientUtils.findBySystemIdentifier(''LOCATION'', code);
    if (resource == null)
    {
      context.missingDhisResource(organizationUnit.getResourceId());
    }
    output.setLocation(null);
    if (typeof output.addLocation !== ''undefined'')
    {
      output.addLocation().getLocation().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
    }
    else
    {
      output.getLocation().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
    }
    ok = true;
  }
}
if (!ok && (output.managingOrganization || output.performer || output.serviceProvider || output.requester))
{
  if (context.getDhisRequest().isDhisFhirId())
  {
    var ref = context.getDhisFhirResourceReference(organizationUnit, ''Organization'');
    if (ref != null)
    {
      if (output.managingOrganization)
      {
        output.setManagingOrganization(ref);
        ok = true;
      }
      else if (output.performer)
      {
        output.setPerformer(null);
        var performer;
        if (typeof output.addPerformer === ''undefined'')
        {
          performer = output.getPerformer();
        }
        else
        {
          performer = output.addPerformer();
        }
        if (typeof performer.actor === ''undefined'')
        {
          performer.setReferenceElement(ref.getReferenceElement());
        }
        else
        {
          performer.setActor(ref);
        }
        ok = true;
      }
      else if (output.serviceProvider)
      {
        output.setServiceProvider(ref);
        ok = true;
      }
      else if (output.requester && output.requester.agent)
      {
        output.setRequester(null);
        output.getRequester().setAgent(ref);
        ok = true;
      }
    }
  }
  else if (((output.managingOrganization && !output.getManagingOrganization().isEmpty()) || (output.performer && !output.getPerformer().isEmpty()) || (output.serviceProvider && !output.getServiceProvider().isEmpty())) && !args[''overrideExisting''])
  {
    ok = true;
  }
  else if ((organizationUnit == null) || (organizationUnit.getCode() == null) || organizationUnit.getCode().isEmpty())
  {
    if (output.managingOrganization)
    {
      output.setManagingOrganization(null);
      ok = true;
    }
    else if (output.performer)
    {
      output.setPerformer(null);
      ok = true;
    }
    else if (output.serviceProvider)
    {
      output.setServiceProvider(null);
      ok = true;
    }
    else if (output.requester && output.requester.agent)
    {
      output.setRequester(null);
      ok = true;
    }
  }
  else
  {
    code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''ORGANIZATION'');
    if ((code == null) && args[''useIdentifierCode''])
    {
      code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''ORGANIZATION'');
    }
  }
  if (code != null)
  {
    var resource = fhirClientUtils.findBySystemIdentifier(''ORGANIZATION'', code);
    if (resource == null)
    {
      context.missingDhisResource(organizationUnit.getResourceId());
    }
    if (output.managingOrganization)
    {
      output.setManagingOrganization(null);
      output.getManagingOrganization().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      ok = true;
    }
    else if (output.performer)
    {
      output.setPerformer(null);
      var performer;
      if (typeof output.addPerformer === ''undefined'')
      {
        performer = output.getPerformer();
      }
      else
      {
        performer = output.addPerformer();
      }
      if (typeof performer.actor === ''undefined'')
      {
        performer.setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      }
      else
      {
        performer.getActor().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      }
      ok = true;
    }
    else if (output.serviceProvider)
    {
      output.setServiceProvider(null);
      output.getServiceProvider().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      ok = true;
    }
    else if (output.requester && output.requester.agent)
    {
      output.setRequester(null);
      output.getRequester().getAgent().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      ok = true;
    }
  }
}
ok' WHERE id = '78c2b73c469c4ab4824407e817b72d4a';

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('41d3bd48578847618274f8327f15fcbe', 0, 'Subject TEI Lookup', 'SUBJECT_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from subject in FHIR Resource.', 'EVALUATE', 'FHIR_RESOURCE', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('41d3bd48578847618274f8327f15fcbe', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('41d3bd48578847618274f8327f15fcbe', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('67249e7b4ba7466ca770a78923fbf1c3', 0, '41d3bd48578847618274f8327f15fcbe', 'fhirResourceUtils.getIdentifiedResource(input.subject, ''Patient'')', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('67249e7b4ba7466ca770a78923fbf1c3', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('67249e7b4ba7466ca770a78923fbf1c3', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('762b4137a98b4b10a0f5629d93e23461', 0, '41d3bd48578847618274f8327f15fcbe',
'Subject TEI Lookup', 'SUBJECT_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from subject in FHIR Resource.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type)
VALUES ('f638ba4c8b5311e989e4e70cb21cbc8c', 0, 'CARE_PLAN_DATE_LOOKUP', 'Care Plan Date Lookup',
'Lookup of the exact date of the FHIR Care Plan.', 'EVALUATE', 'DATE_TIME', 'FHIR_CARE_PLAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f638ba4c8b5311e989e4e70cb21cbc8c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f638ba4c8b5311e989e4e70cb21cbc8c', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('224697268b5411e9b95a93563f739422', 0, 'f638ba4c8b5311e989e4e70cb21cbc8c',
'input.getPeriod().getStart()', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('224697268b5411e9b95a93563f739422', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('0f79f2c88b5411e981f7ebd621029975', 0, 'f638ba4c8b5311e989e4e70cb21cbc8c', 'Care Plan TEI Lookup', 'CARE_PLAN_DATE_LOOKUP',
        'Lookup of the exact date of the FHIR Care Plan');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type)
VALUES ('44d6314675004db7b8bc666c4898523b', 0, 'CARE_PLAN_PROGRAM_REF_LOOKUP', 'Care Plan Tracker Program Lookup',
'Lookup of the Tracker Program of the FHIR Care Plan.', 'EVALUATE', 'PROGRAM_REF', 'FHIR_CARE_PLAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('44d6314675004db7b8bc666c4898523b', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('44d6314675004db7b8bc666c4898523b', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('e13eeaef7a6d4083a233190bc8c1c975', 0, '44d6314675004db7b8bc666c4898523b',
'var programRef = null; if (!input.getInstantiatesUri().isEmpty()) programRef = context.createReference(input.getInstantiatesUri().get(0).getValue(), ''id''); programRef', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('e13eeaef7a6d4083a233190bc8c1c975', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('79d297065c1c47c582456069036072f8', 0, '44d6314675004db7b8bc666c4898523b', 'Care Plan Tracker Program Lookup', 'CARE_PLAN_PROGRAM_REF_LOOKUP',
        'Lookup of the Tracker Program of the FHIR Care Plan.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('e885f646626940c5a8126c2aa081e239', 0, 'DEFAULT_CARE_PLAN_F2D', 'Default FHIR Care Plan to DHIS2 Enrollment Transformation',
'Transforms FHIR Care Plan to DHIS2 Enrollment.', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_CARE_PLAN', 'DHIS_ENROLLMENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e885f646626940c5a8126c2aa081e239', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e885f646626940c5a8126c2aa081e239', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e885f646626940c5a8126c2aa081e239', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('383a5fd3489041b593f36e00c14c2d43', 0, 'e885f646626940c5a8126c2aa081e239',
'function getOutputStatus(input)
{
  var outputStatus = ''CANCELLED'';
  var inputStatus = input.getStatus() == null ? null : input.getStatus().toCode();
  if (inputStatus === ''draft'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''active'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''on-hold'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''revoked'')
  {
    outputStatus = ''CANCELLED'';
  }
  else if (inputStatus === ''completed'')
  {
    outputStatus = ''COMPLETED'';
  }
  else if (inputStatus === ''entered-in-error'')
  {
    outputStatus = ''CANCELLED'';
  }
  return outputStatus;
}
output.setStatus(getOutputStatus(input));
output.setIncidentDate(input.created);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('383a5fd3489041b593f36e00c14c2d43', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('aa956fdb01074cb7b5c1ab6567162d9d', 0, 'e885f646626940c5a8126c2aa081e239', 'Default FHIR Care Plan to DHIS2 Enrollment Transformation', 'DEFAULT_CARE_PLAN_F2D',
        'Transforms FHIR Care Plan to DHIS2 Enrollment.');

INSERT INTO fhir_resource_mapping(id, version, fhir_resource_type, tracked_entity_fhir_resource_type, imp_tei_lookup_script_id, imp_enrollment_org_lookup_script_id, imp_enrollment_date_lookup_script_id)
VALUES('b6650f008462419bb9926775ca0a26cb', 0, 'CARE_PLAN', 'PATIENT', '762b4137a98b4b10a0f5629d93e23461', '25a97bb47b394ed48677db4bcaa28ccf', '0f79f2c88b5411e981f7ebd621029975');

INSERT INTO fhir_rule(id, version, fhir_resource_type, dhis_resource_type, name, description, enabled, imp_enabled, exp_enabled, contained_allowed, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, grouping, evaluation_order, transform_imp_script_id, simple_fhir_id)
VALUES('c4e17e7d880e45b59bc5568da8c79742', 0, 'CARE_PLAN', 'ENROLLMENT', 'Default FHIR Care Plan to DHIS2 Enrollment', 'Default rule that transforms a FHIR Care Plan to a DHIS2 Enrollment.',
true, true, false, false, true, true, true, false, -2147483648, 'aa956fdb01074cb7b5c1ab6567162d9d', TRUE);
INSERT INTO fhir_enrollment_rule(id, program_ref_lookup_script_id) VALUES ('c4e17e7d880e45b59bc5568da8c79742', '79d297065c1c47c582456069036072f8');

UPDATE fhir_rule SET evaluation_order = 100 WHERE fhir_resource_type = 'PATIENT' AND evaluation_order = 0;

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type)
VALUES ('e52272fd0e0c4a74b059e324455721d0', 0, 'QR_DATE_LOOKUP', 'Questionnaire Response Date Lookup',
'Lookup of the exact date of the FHIR Questionnaire Response.', 'EVALUATE', 'DATE_TIME', 'FHIR_QUESTIONNAIRE_RESPONSE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e52272fd0e0c4a74b059e324455721d0', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e52272fd0e0c4a74b059e324455721d0', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('bc694d0666124cbeaac0aa526e25007c', 0, 'e52272fd0e0c4a74b059e324455721d0',
'input.getAuthored()', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('bc694d0666124cbeaac0aa526e25007c', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('521b3a008ecc487ba7e3fe12b68e388a', 0, 'e52272fd0e0c4a74b059e324455721d0', 'Questionnaire Response Date Lookup', 'QR_DATE_LOOKUP',
        'Lookup of the exact date of the FHIR Questionnaire Response.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type)
VALUES ('152434b081db49bba3c53c09caf29208', 0, 'QR_PROGRAM_STAGE_REF_LOOKUP', 'Questionnaire Response Tracker Program Stage Lookup',
'Lookup of the Tracker Program Stage of the FHIR QuestionnaireResponse.', 'EVALUATE', 'PROGRAM_STAGE_REF', 'FHIR_QUESTIONNAIRE_RESPONSE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('152434b081db49bba3c53c09caf29208', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('152434b081db49bba3c53c09caf29208', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('eb2ef8ad93a042d5b8da2d2b0d3c6115', 0, '152434b081db49bba3c53c09caf29208',
'var programStageRef = null; if (input.hasQuestionnaire()) programStageRef = context.createReference(input.getQuestionnaire(), ''id''); programStageRef', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('eb2ef8ad93a042d5b8da2d2b0d3c6115', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('385e52d28674403db42586b5d4e9faf0', 0, '152434b081db49bba3c53c09caf29208', 'Questionnaire Response Tracker Program Stage Lookup',
        'QR_PROGRAM_STAGE_REF_LOOKUP', 'Lookup of the Tracker Program Stage of the FHIR QuestionnaireResponse.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('930a7e03b3224fd9ae0c0213949ac60f', 0, 'DEFAULT_QR_F2D', 'Default FHIR Questionnaire Response to DHIS2 Event Transformation',
'Transforms FHIR Questionnaire Response to DHIS2 Event.', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_QUESTIONNAIRE_RESPONSE', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('930a7e03b3224fd9ae0c0213949ac60f', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('930a7e03b3224fd9ae0c0213949ac60f', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('930a7e03b3224fd9ae0c0213949ac60f', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('330214dbec214cd5b0046592b8f1e2bf', 0, '930a7e03b3224fd9ae0c0213949ac60f',
'function getAnswerFirstValue(item)
{
  var answerValue = null;
  var answers = item.getAnswer();
  if (answers !== null && !answers.isEmpty())
  {
    var answer = answers.get(0);
    if (answer.hasValueDateType())
    {
      answerValue = answer.getValueDateType().getValue();
    }
    else if (answer.hasValueDecimalType())
    {
      answerValue = answer.getValueDecimalType().getValue();
    }
    else if (answer.hasValueBooleanType())
    {
      answerValue = answer.getValueBooleanType().getValue();
    }
    else if (answer.hasValueIntegerType())
    {
      answerValue = answer.getValueIntegerType().getValue();
    }
    else if (answer.hasValueStringType())
    {
      answerValue = answer.getValueStringType().getValue();
    }
    else if (answer.hasValueQuantity())
    {
      answerValue = answer.getValueQuantity().getValue();
    }
    else if (answer.hasValueDateTimeType())
    {
      answerValue = answer.getValueDateTimeType().getValue();
    }
    else if (answer.hasValueTimeType())
    {
      answerValue = answer.getValueTimeType().getValue();
    }
    else if (answer.hasValueTimeType())
    {
      answerValue = answer.getValueTimeType().getValue();
    }
  }
  return answerValue;
}
function getOutputStatus(input)
{
  var outputStatus = ''SKIPPED'';
  var inputStatus = input.getStatus().toCode();
  if (inputStatus === ''in-progress'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''completed'')
  {
    outputStatus = ''COMPLETED'';
  }
  else if (inputStatus === ''amended'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''entered-in-error'')
  {
    outputStatus = ''SKIPPED'';
  }
  else if (inputStatus === ''stopped'')
  {
    outputStatus = ''SKIPPED'';
  }
  return outputStatus;
}
output.setStatus(getOutputStatus(input));
var items = input.getItem();
for (var i = 0; i < items.size(); i++)
{
  var item = items.get(i);
  var linkId = item.getLinkId();
  var answerValue = getAnswerFirstValue(item);
  var linkRef = context.createReference(linkId, ''id'');
  if (programStage.getDataElement(linkRef) != null)
  {
    output.setValue(linkRef, answerValue);
  }
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('330214dbec214cd5b0046592b8f1e2bf', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('2d5ec1318fbc44d395c52544f7ff284f', 0, '930a7e03b3224fd9ae0c0213949ac60f', 'Default FHIR Questionnaire Response to DHIS2 Event Transformation', 'DEFAULT_QR_F2D',
        'Transforms FHIR Questionnaire Response to DHIS2 Event.');

INSERT INTO fhir_resource_mapping(id, version, fhir_resource_type, tracked_entity_fhir_resource_type, imp_tei_lookup_script_id, imp_event_org_lookup_script_id, imp_event_date_lookup_script_id, imp_program_stage_ref_lookup_script_id,
imp_enrollment_org_lookup_script_id, imp_enrollment_date_lookup_script_id)
VALUES('417d0db376bc48bebc4223e7a7e663b0', 0, 'QUESTIONNAIRE_RESPONSE', 'PATIENT', '762b4137a98b4b10a0f5629d93e23461', '25a97bb47b394ed48677db4bcaa28ccf', '521b3a008ecc487ba7e3fe12b68e388a', '385e52d28674403db42586b5d4e9faf0',
'25a97bb47b394ed48677db4bcaa28ccf', '521b3a008ecc487ba7e3fe12b68e388a');

-- Tracker Program Child Programme, Birth: OPV Dose
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT 'e66efbcb7142445a8e1ad1ef524977c1', 0, id, 'CODE:DE_2006104', 'dataElement', true
FROM fhir_rule
WHERE id = '91ae6f5fdb07439197cd407a77794a1b';
DELETE
FROM fhir_executable_script_argument
WHERE id = '4a8ba21510e946f2921fda3973836119';

INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('a55e8da7d71948ddaf74d5f6919ce479', 0, '46f0af46365440b38d4c7a633332c3b3', 'PLAN_DEFINITION', NULL, 'FHIR Plan Definition Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('70a57cdf00af4428b7b3114b408a736b', 0, '46f0af46365440b38d4c7a633332c3b3', 'QUESTIONNAIRE', NULL, 'FHIR Questionnaire Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('edea9a1e83c04ffe801ded5da74d50de', 0, '46f0af46365440b38d4c7a633332c3b3', 'CARE_PLAN', NULL, 'FHIR Care Plan Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('fdc10514ab75468a88136ea41b7c671d', 0, '46f0af46365440b38d4c7a633332c3b3', 'QUESTIONNAIRE_RESPONSE', NULL, 'FHIR Questionnaire Response Resource.', TRUE);

INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('f79b98922b4348af9a823ba03642bac4', 0, 'DHIS2 FHIR Adapter Plan Definition Identifier', 'SYSTEM_DHIS2_FHIR_PLAN_DEFINITION_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/plan-definition-identifier',
        'DHIS2 FHIR Adapter Plan Definition Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('296576d031b544278bf07cc168fb3a82', 0, 'DHIS2 FHIR Adapter Questionnaire Identifier', 'SYSTEM_DHIS2_FHIR_QUESTIONNAIRE_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/questionnaire-identifier',
        'DHIS2 FHIR Adapter Questionnaire Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('9c5584f08b0644eea18f6addf938969f', 0, 'DHIS2 FHIR Adapter Care Plan Identifier', 'SYSTEM_DHIS2_FHIR_CARE_PLAN_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/care-plan-identifier',
        'DHIS2 FHIR Adapter Care Plan Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('5bdff628d94f43dc83678c84fe48bdd9', 0, 'DHIS2 FHIR Adapter Questionnaire Response Identifier', 'SYSTEM_DHIS2_FHIR_QUESTIONNAIRE_R_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/questionnaire-response-identifier',
        'DHIS2 FHIR Adapter Questionnaire Response Identifier.');

INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('69dfbae158734a94862aba7344c383d7', 0, '46f0af46365440b38d4c7a633332c3b3', 'PLAN_DEFINITION', 'f79b98922b4348af9a823ba03642bac4');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('7af9d58d0393411eaa7fe5891418f9f2', 0, '46f0af46365440b38d4c7a633332c3b3', 'QUESTIONNAIRE', '296576d031b544278bf07cc168fb3a82');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('0340bf441eab4c2a98a2aaf7ba35855c', 0, '46f0af46365440b38d4c7a633332c3b3', 'CARE_PLAN', '9c5584f08b0644eea18f6addf938969f');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('789a91bd6bcb4f8496d0c6829b5fdeaa', 0, '46f0af46365440b38d4c7a633332c3b3', 'QUESTIONNAIRE_RESPONSE', '5bdff628d94f43dc83678c84fe48bdd9');

INSERT INTO fhir_rule(id, version, fhir_resource_type, dhis_resource_type, name, description, enabled, imp_enabled, exp_enabled, contained_allowed, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, grouping, evaluation_order, simple_fhir_id)
VALUES('4a9ac195858b455eb34dc560e1855787', 0, 'PLAN_DEFINITION', 'PROGRAM_METADATA', 'Default DHIS2 Program Metadata to FHIR Plan Definition', 'Default rule that transforms a DHIS2 Program Metadata to a FHIR Care Plan.',
true, false, true, false, true, true, true, false, -2147483648, TRUE);
INSERT INTO fhir_program_metadata_rule(id) VALUES ('4a9ac195858b455eb34dc560e1855787');

INSERT INTO fhir_rule(id, version, fhir_resource_type, dhis_resource_type, name, description, enabled, imp_enabled, exp_enabled, contained_allowed, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, grouping, evaluation_order, simple_fhir_id)
VALUES('1f97f85ae4ea46f1bcde77ea178226f4', 0, 'QUESTIONNAIRE', 'PROGRAM_STAGE_METADATA', 'Default DHIS2 Program Stage Metadata to FHIR Questionnaire', 'Default rule that transforms a DHIS2 Program Stage Metadata to a FHIR Questionnaire.',
true, false, true, false, true, true, true, false, -2147483648, TRUE);
INSERT INTO fhir_program_stage_metadata_rule(id) VALUES ('1f97f85ae4ea46f1bcde77ea178226f4');

INSERT INTO fhir_rule(id, version, fhir_resource_type, dhis_resource_type, name, description, enabled, imp_enabled, exp_enabled, contained_allowed, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, grouping, evaluation_order, simple_fhir_id)
VALUES('e54a6c5aef9a4bcfb91129b8b857f58c', 0, 'QUESTIONNAIRE_RESPONSE', 'PROGRAM_STAGE_EVENT', 'Default FHIR Questionnaire Response to DHIS2 Program Stage', 'Default rule that transforms a FHIR Questionnaire Response to a DHIS2 Program Stage.',
true, true, true, false, true, true, true, false, -2147483648, true);
INSERT INTO fhir_program_stage_rule(id) VALUES ('e54a6c5aef9a4bcfb91129b8b857f58c');

UPDATE fhir_rule SET exp_enabled=TRUE WHERE id='c4e17e7d880e45b59bc5568da8c79742';
UPDATE fhir_rule SET transform_imp_script_id=NULL WHERE id='c4e17e7d880e45b59bc5568da8c79742' AND version=0;
UPDATE fhir_enrollment_rule SET program_ref_lookup_script_id=NULL WHERE id='c4e17e7d880e45b59bc5568da8c79742';

UPDATE fhir_script_source SET source_text=
'function getCodeFromHierarchy(hierarchy, resourceType, resourceRef)
{
  var mappedCode = null;
  if (hierarchy != null)
  {
    for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
    {
      var code = identifierUtils.getResourceIdentifier(hierarchy.get(i), resourceType);
      if (code != null)
      {
        mappedCode = codeUtils.getMappedCode(code, resourceType);
        if ((mappedCode == null) && args[''useIdentifierCode''])
        {
          mappedCode = organizationUtils.existsWithPrefix(code);
        }
      }
    }
  }
  return mappedCode;
}
function getOrganizationUnitMappedCode(organizationReference)
{
  var mappedCode = null;
  if (organizationReference != null)
  {
    var hierarchy = organizationUtils.findHierarchy(organizationReference);
    mappedCode = getCodeFromHierarchy(hierarchy, ''Organization'', organizationReference);
  }
  return mappedCode;
}
function getLocationMappedCode(locationReference)
{
  var mappedCode = null;
  if (locationReference != null)
  {
    var hierarchy = locationUtils.findHierarchy(locationReference);
    if (hierarchy != null)
    {
      getCodeFromHierarchy(hierarchy, ''Location'', locationReference);
      for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
      {
        var organizationReference = hierarchy.get(i).getManagingOrganization();
        if (organizationReference != null)
        {
          mappedCode = getOrganizationUnitMappedCode(organizationReference);
        }
      }
    }
  }
  return mappedCode;
}
var fhirOrgUnitRef = null;
var fhirOrgUnitType = null;
if (fhirResourceUtils.hasExtension(input, ''http://www.dhis2.org/dhis2-fhir-adapter/fhir/extensions/location''))
{
  fhirOrgUnitRef = fhirResourceUtils.getExtensionValue(input, ''http://www.dhis2.org/dhis2-fhir-adapter/fhir/extensions/location'');
  if (fhirOrgUnitRef && fhirOrgUnitRef.fhirType() == ''Reference'')
  {
    fhirOrgUnitType = ''Location'';
  }
  else
  {
    fhirOrgUnitRef = null;
  }
}
else if (input.managingOrganization)
{
  fhirOrgUnitRef = input.managingOrganization;
  fhirOrgUnitType = ''Organization'';
}
else if ( (typeof input.locationFirstRep !== ''undefined'') && (typeof input.locationFirstRep.location !== ''undefined'') )
{
  fhirOrgUnitRef = input.locationFirstRep.location;
  fhirOrgUnitType = ''Location'';
}
else if (input.location)
{
  fhirOrgUnitRef = input.location;
  fhirOrgUnitType = ''Location'';
}
else if (input.performer && typeof input.getPerformerFirstRep() !== ''undefined'' && !input.getPerformerFirstRep().isEmpty() && (input.getPerformerFirstRep().getReferenceElement().getResourceType() == null || input.getPerformerFirstRep().getReferenceElement().getResourceType() == ''Organization''))
{
  fhirOrgUnitRef = input.getPerformerFirstRep();
  fhirOrgUnitType = input.getPerformerFirstRep().getReferenceElement().getResourceType();
  if (fhirOrgUnitType == null)
  {
    fhirOrgUnitType = ''Organization'';
  }
}
else if (input.performer && typeof input.getPerformerFirstRep === ''undefined'' && !input.getPerformer().isEmpty() && (input.getPerformer().getReferenceElement().getResourceType() == null || input.getPerformer().getReferenceElement().getResourceType() == ''Organization''))
{
  fhirOrgUnitRef = input.getPerformer();
  fhirOrgUnitType = input.getPerformer().getReferenceElement().getResourceType();
  if (fhirOrgUnitType == null)
  {
    fhirOrgUnitType = ''Organization'';
  }
}
else if (input.serviceProvider)
{
  fhirOrgUnitRef = input.serviceProvider;
  fhirOrgUnitType = ''Organization'';
}
else if (input.author)
{
  fhirOrgUnitRef = input.author;
  fhirOrgUnitType = ''Organization'';
}
else if (input.requester && input.requester.agent)
{
  fhirOrgUnitRef = input.getRequester().getAgent();
  fhirOrgUnitType = input.getRequester().getAgent().getReferenceElement().getResourceType();
  if (fhirOrgUnitType == null)
  {
    fhirOrgUnitType = ''Organization'';
  }
  ok = true;
}
else if (input.encounter)
{
  var encounter = referenceUtils.getResource(input.encounter, ''Encounter'');
  if (encounter != null)
  {
    fhirOrgUnitRef = getLocationReference(encounter.location);
    fhirOrgUnitType = ''Location'';
  }
}
var ref = null;
if (fhirOrgUnitRef != null)
{
  ref = fhirResourceUtils.getAdapterReference(fhirOrgUnitRef, fhirOrgUnitType);
}
if (ref == null)
{
  if (context.getFhirRequest().isDhisFhirId())
  {
    if (fhirOrgUnitRef == null)
    {
      if (args[''useTei''] && (typeof trackedEntityInstance !== ''undefined''))
      {
        ref = context.createReference(trackedEntityInstance.organizationUnitId, ''ID'');
      }
      else if ((typeof enrollment !== ''undefined'') && (enrollment.organizationUnitId != null))
      {
        ref = context.createReference(enrollment.organizationUnitId, ''ID'');
      }
    }
    else
    {
      if (fhirOrgUnitRef != null && fhirOrgUnitRef.identifier !== ''undefined'' && fhirOrgUnitRef.hasIdentifier())
      {
        ref = context.createReference(identifierUtils.getReferenceIdentifier(fhirOrgUnitRef, fhirOrgUnitType), ''code'');
      }
      if (ref == null)
      {
        ref = context.createReference(context.extractDhisId(fhirOrgUnitRef.getReferenceElement()), ''id'');
      }
    }
  }
  else
  {
    var mappedCode = null;
    if (fhirOrgUnitRef != null)
    {
      if (fhirOrgUnitType == ''Location'')
      {
        mappedCode = getLocationMappedCode(fhirOrgUnitRef);
      }
      else
      {
        mappedCode = getOrganizationUnitMappedCode(fhirOrgUnitRef);
      }
    }
    if (mappedCode == null)
    {
      mappedCode = args[''defaultCode''];
    }
    if (mappedCode != null)
    {
      ref = context.createReference(mappedCode, ''CODE'');
    }
    if ((ref == null) && args[''useTei''] && (typeof trackedEntityInstance !== ''undefined''))
    {
      ref = context.createReference(trackedEntityInstance.organizationUnitId, ''ID'');
    }
    else if ((ref == null) && (typeof enrollment !== ''undefined'') && (enrollment.organizationUnitId != null))
    {
      ref = context.createReference(enrollment.organizationUnitId, ''ID'');
    }
  }
}
ref' WHERE id='7b94febabcf64635929a01311b25d975' AND version=0;
