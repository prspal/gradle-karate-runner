import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder

def cucumberJsonToHtml(String jsonFilePath) {
    def jsonSlurper = new JsonSlurper()
    def cucumberResults = jsonSlurper.parse(new File(jsonFilePath))
    
    def writer = new StringWriter()
    def html = new MarkupBuilder(writer)
    
    html.html(lang: 'en') {
        head {
            meta(charset: 'UTF-8')
            meta(name: 'viewport', content: 'width=device-width, initial-scale=1.0')
            title('Cucumber Test Results')
            style('''
                body { font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 20px; }
                .feature-container { margin-bottom: 10px; }
                table { border-collapse: collapse; width: 100%; }
                th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                th { background-color: #f2f2f2; font-weight: bold; }
                .feature-row { background-color: #f9f9f9; }
                .feature-toggle { display: none; }
                .feature-label { display: block; padding: 12px; background-color: #f9f9f9; border: 1px solid #ddd; cursor: pointer; }
                .feature-label::before { content: '▶'; display: inline-block; margin-right: 10px; transition: transform 0.3s ease; }
                .feature-toggle:checked + .feature-label::before { transform: rotate(90deg); }
                .scenario-details { display: none; margin-top: -1px; }
                .feature-toggle:checked ~ .scenario-details { display: table; }
                .pass { color: green; }
                .fail { color: red; }
                .skip { color: orange; }
            ''')
        }
        body {
            cucumberResults.each { feature ->
                def passCount = 0
                def failCount = 0
                def skipCount = 0
                feature.elements.each { scenario ->
                    def scenarioStatus = scenario.steps.every { it.result.status == 'passed' } ? 'pass' :
                                         scenario.steps.any { it.result.status == 'failed' } ? 'fail' : 'skip'
                    switch(scenarioStatus) {
                        case 'pass': passCount++; break
                        case 'fail': failCount++; break
                        case 'skip': skipCount++; break
                    }
                }
                
                div(class: 'feature-container') {
                    input(type: 'checkbox', id: "feature-${feature.id}", class: 'feature-toggle')
                    label(for: "feature-${feature.id}", class: 'feature-label') {
                        mkp.yield(feature.name)
                        span(class: 'pass', " Pass: $passCount")
                        span(class: 'fail', " Fail: $failCount")
                        span(class: 'skip', " Skip: $skipCount")
                    }
                    table(class: 'scenario-details') {
                        thead {
                            tr {
                                th('Scenario Name')
                                th('Status')
                            }
                        }
                        tbody {
                            feature.elements.each { scenario ->
                                tr {
                                    td(scenario.name)
                                    def scenarioStatus = scenario.steps.every { it.result.status == 'passed' } ? 'pass' :
                                                         scenario.steps.any { it.result.status == 'failed' } ? 'fail' : 'skip'
                                    td(class: scenarioStatus, scenarioStatus.capitalize())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    return writer.toString()
}

// Example usage:
// def htmlContent = cucumberJsonToHtml('/path/to/cucumber.json')
// new File('cucumber_results.html').text = htmlContent
