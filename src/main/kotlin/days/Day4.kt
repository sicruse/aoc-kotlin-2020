package days

class Day4 : Day(4) {

    private val passports: List<Map<String, String>> by lazy {
        inputString
            // split the input at blank lines to form record text blocks
            .split("\\n\\n".toRegex())
            .map { record ->
                // for every record text block gather the field text by splitting at whitespace
                record.split("\\s".toRegex())
                    .map { field ->
                        // for every field text entry extract the key / value pairs
                        field.split(':').let { kv ->
                            Pair(kv[0], kv[1])
                        }
                    }
                    // convert the KV pairs into a map for ease of reference
                    .toMap()
            }
    }

    class ValidationRule(val required: Boolean, pattern: String) {
        val regex = pattern.toRegex()
    }

    private val rules = mapOf (
        Pair("byr", ValidationRule(true, "(19[2-9]\\d|200[0-2])")),                         //    byr (Birth Year) - four digits; at least 1920 and at most 2002.
        Pair("iyr", ValidationRule(true,"(201\\d|2020)")),                                  //    iyr (Issue Year) - four digits; at least 2010 and at most 2020.
        Pair("eyr", ValidationRule(true,"(202\\d|2030)")),                                  //    eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
        Pair("hgt", ValidationRule(true,"(1[5-8]\\dcm|19[0-3]cm|59in|6[0-9]in|7[0-6]in)")), //    hgt (Height) - a number followed by either cm or in:
                                                                                                            //    If cm, the number must be at least 150 and at most 193.
                                                                                                            //    If in, the number must be at least 59 and at most 76.
        Pair("hcl", ValidationRule(true, "(#[0-9a-f]{6})")),                                //    hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
        Pair("ecl", ValidationRule(true, "(amb|blu|brn|gry|grn|hzl|oth)")),                 //    ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
        Pair("pid", ValidationRule(true, "([0-9]{9})")),                                    //    pid (Passport ID) - a nine-digit number, including leading zeroes.
        Pair("cid", ValidationRule(false,"")),                                              //    cid (Country ID) - ignored, missing or not.
    )
    private val requiredRules = rules.filter{ it.value.required }
    private val requiredFields = requiredRules.keys
    private val requiredFieldRules = requiredRules.entries.associate { it.key to it.value.regex }

    private val validPassports by lazy {
        passports.count { fields -> fields.keys.containsAll(requiredFields) }
    }

    private val validPassportsWithValidData by lazy {
        passports.count { fields ->
            fields.filter {
                field -> requiredFields.contains(field.key)
            }.count {
                field -> field.value.matches( requiredFieldRules[field.key]!! )
            } == requiredFields.size
        }
    }

    override fun partOne(): Any {
        return validPassports
    }

    override fun partTwo(): Any {
        return validPassportsWithValidData
    }

}
