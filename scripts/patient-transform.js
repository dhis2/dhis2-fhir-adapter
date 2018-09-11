output.organizationUnitId = organizationUtils.getOrganizationUnitId( input.managingOrganization, 'http://example.ph/organizations' );
output.setValueByName( 'National identifier', identifierUtils.getIdentifier( input, 'http://example.ph/national-patient-id' ) );
output.setValueByName( 'Last name', humanNameUtils.getPrimaryName( input.name ).family );
output.setValueByName( 'First name', humanNameUtils.getSingleGiven( humanNameUtils.getPrimaryName( input.name ) ) );
output.setValueByName( 'Birth date', input.birthDate );
output.setValueByName( 'Gender', input.gender );
output.setValueByName( 'Street', addressUtils.getSingleLine( addressUtils.getPrimaryAddress( input.address ) ) );
output.setValueByName( 'City', addressUtils.getPrimaryAddress( input.address ).city );
output.setValueByName( 'State of country', addressUtils.getPrimaryAddress( input.address ).state );
output.setValueByName( 'Country', addressUtils.getPrimaryAddress( input.address ).country );
output.coordinates = geoUtils.getLocation( addressUtils.getPrimaryAddress( input.address ) );

true;