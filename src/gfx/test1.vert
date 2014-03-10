// Necessary Outputs
// 	* gl_Position :

// Available Inputs
//	* gl_Vertex : 4d vector representing vertex potision.
//	* gl_Normal :
//	* gl_Color  : Colour it would be drawn.

// Available Outputs
// 	* gl_FrontColor :
// 	* gl_BackColor  :

varying vec2 texCoord;

void main() {
	gl_Position = ftransform();
	gl_FrontColor = gl_Color;
	texCoord = gl_MultiTexCoord0.xy;
}
