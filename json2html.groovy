import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine

def cucumberJsonToHtml(String jsonFilePath) {
    def jsonSlurper = new JsonSlurper()
    def cucumberResults = jsonSlurper.parse(new File(jsonFilePath))
    
    def engine = new SimpleTemplateEngine()
    def template = engine.createTemplate('''
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cucumber Test Results</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.7.0/chart.min.js"></script>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 20px; }
        .feature-container { margin-bottom: 20px; border: 1px solid #ddd; border-radius: 5px; overflow: hidden; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #f2f2f2; font-weight: bold; }
        .feature-row { background-color: #f9f9f9; }
        .feature-toggle { display: none; }
        .feature-label { display: block; padding: 12px; background-color: #f9f9f9; cursor: pointer; }
        .feature-label::before { content: '▶'; display: inline-block; margin-right: 10px; transition: transform 0.3s ease; }
        .feature-toggle:checked + .feature-label::before { transform: rotate(90deg); }
        .feature-content { display: none; }
        .feature-toggle:checked ~ .feature-content { display: flex; }
        .pass { color: green; }
        .fail { color: red; }
        .skip { color: orange; }
        .chart-container { width: 300px; height: 300px; margin-right: 20px; }
    </style>
</head>
<body>
    <% features.each { feature -> %>
    <div class="feature-container">
        <input type="checkbox" id="feature-${feature.id}" class="feature-toggle">
        <label for="feature-${feature.id}" class="feature-label">
            ${feature.name}
            <span class="pass">Pass: ${feature.passCount}</span>
            <span class="fail">Fail: ${feature.failCount}</span>
            <span class="skip">Skip: ${feature.skipCount}</span>
        </label>
        <div class="feature-content">
            <div class="chart-container">
                <canvas id="chart-${feature.id}"></canvas>
            </div>
            <table>
                <thead>
                    <tr>
                        <th>Scenario Name</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <% feature.scenarios.each { scenario -> %>
                    <tr>
                        <td>${scenario.name}</td>
                        <td class="${scenario.status.toLowerCase()}">${scenario.status}</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <% } %>

    <script>
    <% features.each { feature -> %>
        new Chart(document.getElementById('chart-${feature.id}').getContext('2d'), {
            type: 'pie',
            data: {
                labels: ['Pass', 'Fail', 'Skip'],
                datasets: [{
                    data: [${feature.passCount}, ${feature.failCount}, ${feature.skipCount}],
                    backgroundColor: ['#4CAF50', '#F44336', '#FFC107']
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: 'Scenario Results'
                    }
                }
            }
        });
    <% } %>
    </script>
</body>
</html>
    ''')
    
    def processedFeatures = cucumberResults.collect { feature ->
        def scenarios = feature.elements.collect { scenario ->
            def scenarioStatus = scenario.steps.every { it.result.status == 'passed' } ? 'Pass' :
                                 scenario.steps.any { it.result.status == 'failed' } ? 'Fail' : 'Skip'
            [name: scenario.name, status: scenarioStatus]
        }
        def passCount = scenarios.count { it.status == 'Pass' }
        def failCount = scenarios.count { it.status == 'Fail' }
        def skipCount = scenarios.count { it.status == 'Skip' }
        
        [
            id: feature.id,
            name: feature.name,
            passCount: passCount,
            failCount: failCount,
            skipCount: skipCount,
            scenarios: scenarios
        ]
    }
    
    return template.make([features: processedFeatures]).toString()
}

// Example usage:
// def htmlContent = cucumberJsonToHtml('/path/to/cucumber.json')
// new File('cucumber_results.html').text = htmlContent
