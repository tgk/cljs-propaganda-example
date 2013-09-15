goog.provide('example.components.Visualisation');

example.components.Visualisation = function(elem, width, height) {
  this.elem = elem;
  this.width = width;
  this.height = height;

  this.svg = d3.select(this.elem).append("svg")
    .attr("width", this.width)
    .attr("height", this.height)
    .append("g");

  this.svg.append("rect")
    .attr("width", this.width)
    .attr("height", this.height);

  this.node = this.svg.selectAll(".node");
};

example.components.Visualisation.prototype.update = function(states, valueAreas) {

  var x = d3.scale.linear()
                .domain([0, states.length - 1])
                .range([90, this.width - 90]);

  this.node = this.node.data(states);

  var tooltipY = this.height - 40;

  this.node.enter()
    .insert("circle", ".node")
    .attr("class", "node")
    .classed("stabile",   function(d) { return  d.stabile; })
    .classed("unstabile", function(d) { return !d.stabile; })
    .attr("r", function(d) { return d.stabile ? 4 : 2; })
    .attr("cx", function(d, i) { return x(i); })
    .attr("cy", this.height * 3 / 2)
    .on("mousemove", function(d) {
        var xPosition = d3.select(this).attr("cx") - 170 / 2;
        var yPosition = tooltipY;

        d3.select("#tooltip")
            .style("left", xPosition + "px")
            .style("top", yPosition + "px");
        d3.select("#tooltip #values")
            .text(d.step + "\n" + d.values);
        d3.select("#tooltip").classed("hidden", false);
    })
    .on("mouseout", function() {
        d3.select("#tooltip").classed("hidden", true);
    });

  this.node.exit()
    .transition()
    .duration(1000)
    .attr("cy", this.height * 3 /2)
    .each("end", function() { d3.select(this).remove(); });

  this.node
    .transition()
    .duration(1000)
    .attr("cx", function(d, i) { return x(i); })
    .attr("cy", this.height * 3 / 4);

    // Show area of possible values
    var valueAreaColorScale = d3.scale.category20();
    var valueAreasCount = 0;
    for (var p in valueAreas) { valueAreasCount++; }
    var individualHeight = (200.0 - 20.0) / valueAreasCount;

    var i = 0;
    for (var name in valueAreas) {
        var valueArea = valueAreas[name];

        var y = d3.scale.linear()
                .domain([valueArea.values[0][0], valueArea.values[0][1]])
                .range([20 + i * individualHeight,
                        20 + (i + 1) * individualHeight]);

        var valueLine = d3.svg.area()
                .x(function(d, i) { return x(i + valueArea.offset); })
                .y0(function(d) { return y(d[0]); })
                .y1(function(d) { return y(d[1]); })
                .interpolate("linear");

        this.svg.append("svg:path")
            .attr("id", "valueArea" + name)
            .attr("d", valueLine(valueArea.values))
            .attr("opacity", 1)
            .attr("fill", valueAreaColorScale(name));

        this.svg.append("text")
            .attr("x", 10)
            .attr("y", y((valueArea.values[0][0] + valueArea.values[0][1]) / 2))
            .attr("fill", valueAreaColorScale(name))
            .text(name);

        i++;
    }
};
