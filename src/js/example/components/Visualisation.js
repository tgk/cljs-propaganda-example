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

example.components.Visualisation.prototype.update = function(states) {

  var scale = d3.scale.linear()
                .domain([0, states.length - 1])
                .range([90, this.width - 90]);

  this.node = this.node.data(states);

  var tooltipY = this.height / 2 + 22;

  this.node.enter()
    .insert("circle", ".node")
    .attr("class", "node")
    .classed("stabile",   function(d) { return  d.stabile; })
    .classed("unstabile", function(d) { return !d.stabile; })
    .attr("r", function(d) { return d.stabile ? 5 : 4; })
    .attr("cx", function(d, i) { return scale(i); })
    .attr("cy", this.height * 3 / 2)
    .on("mousemove", function(d) {
        var xPosition = d3.select(this).attr("cx") - 170 / 2;
        var yPosition = tooltipY;

        d3.select("#tooltip")
            .style("left", xPosition + "px")
            .style("top", yPosition + "px");
        d3.select("#tooltip #values")
            .text(d.values);
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
    .attr("cx", function(d, i) { return scale(i); })
    .attr("cy", this.height / 2);
};
