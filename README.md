# twig-beautify

This is a formatter for twig code.

It can:
- Beautify one line code with proper newlines and indents.
- Remove all indents and newlines to convert to one line.
- Add whitespace control characters (-) to all tags so spaces don't print in html forms.

When you beautify, it also removes any leading or trailing concatenation characters (~) where they're not meant to be.