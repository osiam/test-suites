--
-- Extension data for registration purpose
--

INSERT INTO scim_extension VALUES (5, 'urn:org.osiam:scim:extensions:stress-tests');

INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
	VALUES (6, false, 'gender', 'STRING', 5);
INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
	VALUES (7, false, 'age', 'INTEGER', 5);
