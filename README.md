# WhatTo

A modern, interactive JavaFX desktop app for managing tasks, notes, and checklists with a beautiful, animated UI.

## Features

- **LIFO Badge Stack:** New badges appear at the top, older ones move down.
- **Animated UI:** Smooth hover, pop-in, and swipe-to-delete animations for badges and buttons.
- **Checklists:**
  - Use markdown-like syntax (`- [ ] item` or `- [x] item`) for interactive checklists.
  - Check/uncheck items directly in the badge view.
  - Checklist state is preserved even after editing.
- **Badge Editing:**
  - Right-click a badge to edit its title and body.
  - The body editor auto-grows to fit your text.
  - Checklist markdown (`[ ]`/`[x]`) is hidden while editing for a clean experience.
  - When you save, checklist formatting is restored.
- **Swipe to Delete:** Drag a badge left to remove it with an animation.
- **Modern Scroll Area:**
  - Seamless, styled scroll area with fade effects at the top and bottom.
  - Scroll position is preserved when checking/unchecking items.

## How It Works

- **Adding a Badge:**
  - Click the "Add Badge" button. The new badge appears at the top.
- **Checklists:**
  - In the badge body, start a line with `-` and a space to create a checklist item.
  - When you save, lines starting with `- ` become interactive checkboxes.
  - Checked items are saved as `- [x] item`, unchecked as `- [ ] item`.
- **Editing:**
  - Right-click a badge to edit.
  - The title and body are editable. Checklist brackets are hidden while editing.
  - Press Enter after a checklist line to auto-continue the checklist.
- **Deleting:**
  - Drag a badge left to delete it.

## Usage

1. **Run the app** (JavaFX required).
2. Click "Add Badge" to create a new badge.
3. Right-click a badge to edit its title/body.
4. Use `-` and a space to create checklist items in the body.
5. Check/uncheck items directly in the badge view.
6. Drag a badge left to delete it.

## Customization

- **Colors and Animations:**
  - Tweak `Styling.css` for color schemes and animation speeds.
- **Checklist Logic:**
  - The app uses a custom, minimal markdown-like parser for checklists.
  - Only checklist syntax is supported; other markdown features are not parsed.

---

**Built with JavaFX.**

Feel free to fork, modify, and use for your own productivity workflows!
