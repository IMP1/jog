// Necessary Outputs
// 	* gl_FragColor : colour to actually draw the fragment.

// Available Inputs
//	* gl_Color		 : colour it would be drawn.
//	* gl_FragCoord   : location of fragment in window space.
//	* gl_PointCoord  :

const vec2 resolution = vec2(640, 480);
uniform float timeElapsed;

void main() {
	float t = timeElapsed;
	float f=3, g=3;
	float m1 = sin(t * 0.3) * sin(t) + sin(t * 0.3);
	float m2 = (1 - cos(t * 0.628)) * sin(t) + cos(t * 0.3) + 1;
	vec2 m = vec2(m1, m2) * resolution.xy;
	vec2 z = (-resolution.xy + 2 * gl_FragCoord.xy) / resolution.y;
	vec2 p = (-resolution.xy + 2 + m) / resolution.y;
	for (int i=0; i<10; i++){
		float d = dot(z,z);
		z = (vec2(z.x,-z.y)/d) + p;
		z.x = abs(z.x);
		f = max(f, (dot(z-p, z-p)));
		g = min(g, sin(mod(dot(z+p, z+p), 6))+1);
	}
	f = abs(-log(f) / 2);
	g = abs(-log(g) / 8);
	gl_FragColor = vec4(min(vec3(g, g * f, f), 1), 1);
}
